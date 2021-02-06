package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.math.DoublePowerSeries;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.Vector;

public class TangentialDistortion<V extends Vector<V>> extends Distortion<V> {
    public final double centerX;
    public final double centerY;
    public final double tangentialCoefficient1;
    public final double tangentialCoefficient2;
    public final DoublePowerSeries radialPartCoefficients;

    public TangentialDistortion(
            double centerX,
            double centerY,
            double tangentialCoefficient1,
            double tangentialCoefficient2,
            double... radialPartCoefficients
    ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.tangentialCoefficient1 = tangentialCoefficient1;
        this.tangentialCoefficient2 = tangentialCoefficient2;
        this.radialPartCoefficients = new DoublePowerSeries(
                new DoublePowerSeries(radialPartCoefficients)
                        .moveRight()
                        .plus(new DoublePowerSeries(new double[]{1.0}))
        );
    }


    @Override
    protected Direction distort(int x, int y) {
        double dx = x - centerX;
        double dy = y - centerY;
        double rSquared = dx * dx + dy * dy;
        double rCoefficient = radialPartCoefficients.valueAt(rSquared);

        return new Direction(
                dx + rCoefficient * (
                        tangentialCoefficient2 * (rSquared + 2 * dx * dx) +
                                tangentialCoefficient1 * 2 * dx * dy
                ) + centerX,
                dy + rCoefficient * (
                        tangentialCoefficient1 * (rSquared + 2 * dy * dy) +
                                tangentialCoefficient2 * 2 * dx * dy
                ) + centerY
        );
    }
}
