package dyomin.mikhail.vision.math;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class DoublePowerSeries extends PowerSeries<NumericDouble> {
    public DoublePowerSeries(double[] coefficients) {
        super(NumericDouble.ZERO, Arrays.stream(coefficients).mapToObj(NumericDouble::new).collect(Collectors.toList()));
    }

    public DoublePowerSeries(PowerSeries<NumericDouble> other) {
        this(other.getCoefficients().mapToDouble(numericDouble -> numericDouble.value).toArray());
    }

    public DoubleStream getDoubleCoefficients() {
        return getCoefficients().mapToDouble(numericDouble -> numericDouble.value);
    }

    public double valueAt(double point) {
        return valueAt(new NumericDouble(point)).value;
    }

    public static DoublePowerSeries withRoots(double[] roots) {
        return new DoublePowerSeries(PowerSeries.withRoots(
                NumericDouble.ZERO,
                NumericDouble.ONE,
                Arrays.stream(roots)
                        .mapToObj(NumericDouble::new)
                        .toArray(NumericDouble[]::new)
        ));
    }
}
