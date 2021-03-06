package dyomin.mikhail.vision.math.powerseries;

import Jama.Matrix;
import dyomin.mikhail.vision.math.numeric.NumericDouble;
import dyomin.mikhail.vision.math.numeric.factory.DoubleFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class DoublePowerSeries extends PowerSeriesBase<NumericDouble, NumericDouble, DoublePowerSeries> {
    public DoublePowerSeries(double... coefficients) {
        this(Arrays.stream(coefficients).mapToObj(NumericDouble::new).collect(Collectors.toList()));
    }

    public DoublePowerSeries(List<NumericDouble> coefficients) {
        super(DoubleFactory.FACTORY, DoubleFactory.FACTORY, coefficients);
    }

    public static DoublePowerSeries withRoots(double[] roots) {
        return PowerSeriesBase.withRoots(
                DoublePowerSeries::new,
                DoubleFactory.FACTORY,
                Arrays.stream(roots).mapToObj(NumericDouble::new).toArray(NumericDouble[]::new)
        );
    }

    public static DoublePowerSeries ofPoints(Map<Double, Double> points) {
        return PowerSeriesBase.<NumericDouble, NumericDouble, DoublePowerSeries>
                ofPoints(
                DoublePowerSeries::new,
                DoubleFactory.FACTORY,
                DoubleFactory.FACTORY,
                points.entrySet().stream().collect(Collectors.toMap(
                        k -> new NumericDouble(k.getKey()),
                        k -> new NumericDouble(k.getValue())
                ))
        );
    }

    @Override
    protected DoublePowerSeries buildFromCoefficients(List<NumericDouble> coefficients) {
        return new DoublePowerSeries(coefficients);
    }

    public DoubleStream getDoubleCoefficients() {
        return getCoefficients().mapToDouble(numericDouble -> numericDouble.value);
    }

    public double valueAt(double point) {
        return valueAt(new NumericDouble(point)).value;
    }

    /**
     * let <br>
     * g(x) = 0 + g1*x + g2*x^2 + ... + gn*x^n<br>
     * f(x) = 0 + f1*x + f2*x^2 + ... + fn*x^n<br>
     * <br>
     * F = [f1, ... fn], G = [g1, ... gn] <br>
     * <br>
     * then we will find the result as a solution of <br>
     * F_* = argmin_F {integral from -c to c of (f(g(x)) - x)^2 dx}<br>
     * using Newton's optimisation method. In our case this will give the result after a single iteration.
     *
     * @param integrationLimit constant c, representing absolute value of integration bounds
     * @param resultMaxPower   - preferred number of coefficients in result
     * @return power series f with max(n, resultMaxPower) coefficients such that f.valueAt(this.valueAt(a)) ~==~ a
     */
    public DoublePowerSeries optimalCompInverse(double integrationLimit, int resultMaxPower) {
        int resultPower = Math.max(resultMaxPower, coefficients.size() - 1);
        List<DoublePowerSeries> powersOfThis = new ArrayList<>(resultPower);

        DoublePowerSeries thisRaisedToI = new DoublePowerSeries(this.coefficients);

        for (int i = 1; i <= resultPower * 2; i++) {
            powersOfThis.add(thisRaisedToI);
            thisRaisedToI = thisRaisedToI.multiply(this);
        }

        double[] gradientVector = new double[resultPower];

        for (int i = 0; i < resultPower; i++) {
            DoublePowerSeries integral = powersOfThis.get(i)
                    .moveRight()
                    .multiplyByCoefficient(coefficientFactory.fromInteger(-2))
                    .integrate(coefficientFactory.getZero());
            double v1 = integral.valueAt(integrationLimit);
            double v2 = integral.valueAt(-integrationLimit);
            gradientVector[i] = v1 - v2;
        }

        Matrix grad = new Matrix(new double[][]{gradientVector});

        double[] hessianValues = powersOfThis.stream().mapToDouble(series -> {
            DoublePowerSeries integral = series
                    .multiplyByCoefficient(coefficientFactory.fromInteger(2))
                    .integrate(coefficientFactory.getZero());
            return integral.valueAt(integrationLimit) - integral.valueAt(-integrationLimit);
        }).toArray();

        Matrix hessian = new Matrix(resultPower, resultPower);

        for (int row = 0; row < resultPower; row++) {
            for (int col = 0; col < resultPower; col++) {
                hessian.set(row, col, hessianValues[row + col + 1]);
            }
        }

        Matrix inverseHessian = hessian.inverse();

        return new DoublePowerSeries(Stream.concat(
                Stream.of(coefficientFactory.getZero()),
                Arrays.stream(grad.times(inverseHessian).times(-1).getArray()[0])
                        .mapToObj(NumericDouble::new)
        ).collect(Collectors.toList()));
    }
}
