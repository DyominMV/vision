package dyomin.mikhail.vision.geometry;

public class HcPoint extends HcVector {
    public HcPoint(double cx, double cy, double c) {
        super(cx, cy, c);
    }

    public HcPoint(double x, double y) {
        super(x, y);
    }

    public HcLine lineTo(HcPoint other) {
        return new HcLine(
                this.cy * other.c - this.c * other.cy,
                this.c * other.cx - this.cx * other.c,
                this.cx * other.cy - this.cy * other.cx
        );
    }

    public double spaceTo(HcPoint other) {
        return Math.sqrt(this.x() * other.x() + this.y() * other.y());
    }

    public double spaceTo(HcLine line) {
        double numerator = this.cx * line.cx + this.cy * line.cy + this.c * line.c;
        double denominator = this.c * Math.sqrt(line.cx * line.cx + line.cy * line.cy);
        return numerator / denominator;
    }

}
