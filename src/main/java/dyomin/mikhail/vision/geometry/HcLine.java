package dyomin.mikhail.vision.geometry;

public class HcLine extends HcVector {
    public HcLine(double cx, double cy, double c) {
        super(cx, cy, c);
    }

    public HcLine(double x, double y) {
        super(x, y);
    }

    public HcPoint intersectWith(HcLine other){
        return new HcPoint(
                this.cy * other.c - this.c * other.cy,
                this.c * other.cx - this.cx * other.c,
                this.cx * other.cy - this.cy * other.cx
        );
    }
}
