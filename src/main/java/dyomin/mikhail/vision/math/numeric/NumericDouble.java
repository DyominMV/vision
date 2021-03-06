package dyomin.mikhail.vision.math.numeric;

public class NumericDouble implements Numeric<NumericDouble> {
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
    public NumericDouble multiplyByNumeric(NumericDouble numeric) {
        return multiply(numeric);
    }

    @Override
    public NumericDouble negate() {
        return new NumericDouble(-value);
    }

    @Override
    public NumericDouble invert() {
        return new NumericDouble(1 / value);
    }

    @Override
    public String toString() {
        return "(" + value + ")";
    }
}
