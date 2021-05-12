package dyomin.mikhail.vision.vectors;

public class Disparity implements Vector<Disparity> {
    public final double value;

    public static Disparity NULL() {
        return new Disparity(Double.NaN);
    }

    public boolean isNull() {
        return this.value != this.value;
    }

    public Disparity(double value) {
        this.value = Math.abs(value);
    }

    @Override
    public Disparity plus(Disparity other) {
        return new Disparity(this.value + other.value);
    }

    @Override
    public Disparity minus(Disparity other) {
        return new Disparity(this.value - other.value);
    }

    @Override
    public Disparity amplify(double coefficient) {
        return new Disparity(this.value * coefficient);
    }

    @Override
    public double length() {
        return this.value;
    }

    @Override
    public RGB visualize() {
        return isNull()
                ? new RGB(-1, -1, -1)
                : RGB.gray(1 - 2.0 / (1+value/80));
    }
}
