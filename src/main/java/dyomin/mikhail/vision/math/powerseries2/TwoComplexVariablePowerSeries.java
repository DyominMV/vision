package dyomin.mikhail.vision.math.powerseries2;

import dyomin.mikhail.vision.math.numeric.Complex;
import dyomin.mikhail.vision.math.numeric.factory.ComplexFactory;
import dyomin.mikhail.vision.math.powerseries.ComplexPowerSeries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TwoComplexVariablePowerSeries extends TwoVariablePowerSeriesBase<
        Complex,
        ComplexPowerSeries,
        TwoComplexVariablePowerSeries
        > {

    public TwoComplexVariablePowerSeries(Complex[][] coefficients) {
        this(Arrays.stream(coefficients).map(Arrays::asList).collect(Collectors.toList()));
    }

    public TwoComplexVariablePowerSeries(List<List<Complex>> coefficients) {
        this(
                coefficients.stream()
                        .map(ComplexPowerSeries::new)
                        .collect(Collectors.toList())
                , 1
        );
    }

    private TwoComplexVariablePowerSeries(List<ComplexPowerSeries> listOfSeries, int any) {
        super(ComplexFactory.FACTORY, listOfSeries);
    }

    @Override
    protected TwoComplexVariablePowerSeries buildFromCoefficients(List<ComplexPowerSeries> coefficients) {
        return new TwoComplexVariablePowerSeries(coefficients, 0);
    }

    @Override
    protected ComplexPowerSeries getZeroCoefficient() {
        return new ComplexPowerSeries();
    }

    @Override
    protected ComplexPowerSeries buildRegularPowerSeries(List<Complex> numerics) {
        return new ComplexPowerSeries(numerics);
    }
}
