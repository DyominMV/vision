package dyomin.mikhail.vision.math;

public class NumericDouble implements Numeric<NumericDouble>{
    public final double value;

    public NumericDouble(double value) {
        this.value = value;
    }

    @Override
    public NumericDouble plus(NumericDouble other) {
        return new NumericDouble(this.value + other.value);
    }

    @Override
    public NumericDouble multiply(NumericDouble other) {
        return new NumericDouble(this.value * other.value);
    }

    @Override
    public NumericDouble negate() {
        return new NumericDouble(-value);
    }

    @Override
    public NumericDouble invert() {
        return new NumericDouble(1/value);
    }

    public static final NumericDouble ZERO = new NumericDouble(0);
    public static final NumericDouble ONE = new NumericDouble(1);
}
