package dyomin.mikhail.vision.math.powerseries;

import dyomin.mikhail.vision.math.numeric.NumericDouble;
import dyomin.mikhail.vision.math.numeric.factory.NumericDoubleFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class DoublePowerSeries extends PowerSeriesBase<NumericDouble, DoublePowerSeries> {
    public DoublePowerSeries(double... coefficients) {
        this(Arrays.stream(coefficients).mapToObj(NumericDouble::new).collect(Collectors.toList()));
    }

    public DoublePowerSeries(List<NumericDouble> coefficients) {
        super(NumericDoubleFactory.FACTORY, coefficients);
    }

    public static DoublePowerSeries withRoots(Double[] roots) {
        return PowerSeriesBase.withRoots(
                DoublePowerSeries::new,
                NumericDoubleFactory.FACTORY,
                Arrays.stream(roots).map(NumericDouble::new).toArray(NumericDouble[]::new)
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
}
