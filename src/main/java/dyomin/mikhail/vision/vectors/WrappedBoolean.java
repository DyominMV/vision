package dyomin.mikhail.vision.vectors;

public class WrappedBoolean implements Vector<WrappedBoolean> {

    public final boolean value;

    public WrappedBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public WrappedBoolean plus(WrappedBoolean other) {
        return new WrappedBoolean(this.value || other.value);
    }

    @Override
    public WrappedBoolean minus(WrappedBoolean other) {
        return new WrappedBoolean(this.value && (!other.value));
    }

    @Override
    public WrappedBoolean amplify(double coefficient) {
        return this;
    }

    @Override
    public double length() {
        return this.value ? 1 : 0;
    }

    @Override
    public RGB visualize() {
        return RGB.gray(this.value ? 1 : -1);
    }
}
