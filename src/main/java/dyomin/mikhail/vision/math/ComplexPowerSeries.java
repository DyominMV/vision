package dyomin.mikhail.vision.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComplexPowerSeries extends PowerSeries<Complex> {
    public ComplexPowerSeries(Complex... coefficients) {
        super(Complex.ZERO, Arrays.asList(coefficients.clone()));
    }

    public ComplexPowerSeries(List<Complex> coefficients) {
        this(coefficients.toArray(new Complex[0]));
    }

    public ComplexPowerSeries(PowerSeries<Complex> other){
        this(new ArrayList<>(other.coefficients));
    }

    public static ComplexPowerSeries withRoots(Complex[] roots) {
        return new ComplexPowerSeries(PowerSeries.withRoots(Complex.ZERO, Complex.ONE, roots).coefficients);
    }
}
