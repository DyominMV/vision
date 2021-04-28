package dyomin.mikhail.vision.vectors;

public class WrappedDouble implements Vector<WrappedDouble> {
    public final double value;

    private static final double VISUALISING_COEFFICIENT = 1;

    public WrappedDouble(double value) {
        this.value = value;
    }

    public WrappedDouble(Number value) {
        this(value.doubleValue());
    }

    @Override
    public WrappedDouble plus(WrappedDouble other) {
        return new WrappedDouble(this.value + other.value);
    }

    @Override
    public WrappedDouble minus(WrappedDouble other) {
        return new WrappedDouble(this.value - other.value);
    }

    @Override
    public WrappedDouble amplify(double coefficient) {
        return new WrappedDouble(this.value * coefficient);
    }

    @Override
    public double length() {
        return Math.abs(this.value);
    }

    @Override
    public RGB visualize() {
        return RGB.gray(this.value / VISUALISING_COEFFICIENT);
    }
}
