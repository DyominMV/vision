package dyomin.mikhail.vision.math.powerseries2;

import dyomin.mikhail.vision.math.numeric.NumericDouble;
import dyomin.mikhail.vision.math.numeric.factory.NumericDoubleFactory;
import dyomin.mikhail.vision.math.powerseries.DoublePowerSeries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class TwoDoubleVariablePowerSeries extends TwoVariablePowerSeriesBase<
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
        super(NumericDoubleFactory.FACTORY, listOfSeries);
    }

    @Override
    protected TwoDoubleVariablePowerSeries buildFromCoefficients(List<DoublePowerSeries> coefficients) {
        return new TwoDoubleVariablePowerSeries(coefficients, 0);
    }

    @Override
    protected DoublePowerSeries getZeroCoefficient() {
        return new DoublePowerSeries();
    }

    @Override
    protected DoublePowerSeries buildRegularPowerSeries(List<NumericDouble> numerics) {
        return new DoublePowerSeries(numerics);
    }

    public double valueAt(double pointX, double pointY) {
        return valueAt(new NumericDouble(pointX), new NumericDouble(pointY)).value;
    }

    public Stream<DoubleStream> getDoubleCoefficients() {
        return getNumericCoefficients().map(numerics -> numerics.mapToDouble(numeric -> numeric.value));
    }

}
