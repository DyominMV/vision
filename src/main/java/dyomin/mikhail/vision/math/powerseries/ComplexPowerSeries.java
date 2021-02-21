package dyomin.mikhail.vision.math.powerseries;

import dyomin.mikhail.vision.math.numeric.Complex;
import dyomin.mikhail.vision.math.numeric.factory.ComplexFactory;

import java.util.Arrays;
import java.util.List;

public class ComplexPowerSeries extends PowerSeriesBase<Complex, Complex, ComplexPowerSeries> {
    public ComplexPowerSeries(Complex... coefficients) {
        this(Arrays.asList(coefficients));
    }

    public ComplexPowerSeries(List<Complex> coefficients) {
        super(ComplexFactory.FACTORY, coefficients);
    }

    public static ComplexPowerSeries withRoots(Complex[] roots) {
        return PowerSeriesBase.withRoots(
                ComplexPowerSeries::new,
                ComplexFactory.FACTORY,
                roots
        );
    }

    @Override
    protected ComplexPowerSeries buildFromCoefficients(List<Complex> coefficients) {
        return new ComplexPowerSeries(coefficients);
    }

    @Override
    protected Complex getZeroCoefficient() {
        return ComplexFactory.FACTORY.getZero();
    }
}
