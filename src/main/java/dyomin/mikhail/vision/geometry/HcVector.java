package dyomin.mikhail.vision.geometry;

import dyomin.mikhail.vision.vectors.Vector;

public class HcVector {
    public final double cx;
    public final double cy;
    public final double c;

    public double x() {
        return cx / c;
    }

    public double y() {
        return cy / c;
    }

    public HcVector(double cx, double cy, double c) {
        this.cx = cx;
        this.cy = cy;
        this.c = c;
    }

    public HcVector(double x, double y) {
        this.cx = x;
        this.cy = y;
        this.c = 1;
    }

    public boolean isValid() {
        return Double.MIN_NORMAL <= Math.abs(c);
    }
}
