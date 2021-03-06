package dyomin.mikhail.vision.vectors;

public class Direction implements Vector<Direction> {
    public final double x;
    public final double y;

    public Direction(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Direction plus(Direction other) {
        return new Direction(this.x + other.x, this.y + other.y);
    }

    @Override
    public Direction minus(Direction other) {
        return new Direction(this.x - other.x, this.y - other.y);
    }

    @Override
    public Direction amplify(double coefficient) {
        return new Direction(this.x * coefficient, this.y * coefficient);
    }

    @Override
    public double product(Direction other) {
        return this.x * other.x + this.y * other.y;
    }

    @Override
    public double length() {
        return Math.sqrt(this.product(this));
    }

    public double angle() {
        double angle = Math.atan2(y, x);
        return angle < 0
                ? angle + 2 * Math.PI
                : angle;
    }

    @Override
    public RGB visualize() {
        return new RGB(x, y, 0);
    }
}
