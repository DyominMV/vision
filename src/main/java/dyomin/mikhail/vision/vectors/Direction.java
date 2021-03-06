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
    public double length() {
        return Math.sqrt(x * x + y * y);
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

    public static Direction ofLengthAndAngle(double length, double angle) {
        return new Direction(length * Math.cos(angle), length * Math.sin(angle));
    }

    public Direction normalize() {
        return this.length() <= Double.MIN_NORMAL
                ? new Direction(0, 0)
                : new Direction(x / this.length(), y / this.length());
    }

    public Direction rotate(double angle){
        return Direction.ofLengthAndAngle(this.length(), this.angle() + angle);
    }

    public Direction setLength(double length){
        return Direction.ofLengthAndAngle(length, this.angle());
    }
}
