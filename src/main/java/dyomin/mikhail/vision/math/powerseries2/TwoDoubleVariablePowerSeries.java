package dyomin.mikhail.vision.math.powerseries2;

import dyomin.mikhail.vision.math.numeric.NumericDouble;
import dyomin.mikhail.vision.math.numeric.factory.DoubleFactory;
import dyomin.mikhail.vision.math.powerseries.DoublePowerSeries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class TwoDoubleVariablePowerSeries extends TwoVariablePowerSeriesBase<
        NumericDouble,
        NumericDouble,
        DoublePowerSeries,
        TwoDoubleVariablePowerSeries
        > {

    public TwoDoubleVariablePowerSeries(double[][] coefficients) {
        this(Arrays.stream(coefficients)
                .map(ds -> Arrays.stream(ds).mapToObj(NumericDouble::new).collect(Collectors.toList()))
                .collect(Collectors.toList()));
    }

    public TwoDoubleVariablePowerSeries(List<List<NumericDouble>> coefficients) {
        this(coefficients.stream().map(DoublePowerSeries::new).collect(Collectors.toList()), 0);
    }

    private TwoDoubleVariablePowerSeries(List<DoublePowerSeries> listOfSeries, int any) {
        super(DoubleFactory.FACTORY, DoubleFactory.FACTORY, DoublePowerSeries::new, listOfSeries);
    }

    @Override
    protected TwoDoubleVariablePowerSeries buildFromCoefficients(List<DoublePowerSeries> coefficients) {
        return new TwoDoubleVariablePowerSeries(coefficients, 0);
    }

    public double valueAt(double pointX, double pointY) {
        return valueAt(new NumericDouble(pointX), new NumericDouble(pointY)).value;
    }

    public Stream<DoubleStream> getDoubleCoefficients() {
        return getSimpleCoefficients().map(numerics -> numerics.mapToDouble(numeric -> numeric.value));
    }

    public double integral(double fromX, double toX, double fromY, double toY) {
        DoublePowerSeries integratedByYX = new DoublePowerSeries(
                getCoefficients().mapToDouble(ps -> {
                            DoublePowerSeries integral = ps.integrate(new NumericDouble(0));
                            return integral.valueAt(toY) - integral.valueAt(fromY);
                        }
                ).toArray()
        ).integrate(new NumericDouble(0));

        return integratedByYX.valueAt(toX) - integratedByYX.valueAt(fromX);
    }

}
