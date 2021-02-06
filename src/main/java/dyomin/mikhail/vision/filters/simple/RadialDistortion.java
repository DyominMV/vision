package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.math.DoublePowerSeries;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.Vector;

public class RadialDistortion<V extends Vector<V>> extends Distortion<V> {
    private final DoublePowerSeries distortionCoefficients;
    private final double centerX;
    private final double centerY;

    public RadialDistortion(double centerX, double centerY, double... coefficients) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.distortionCoefficients = new DoublePowerSeries(
                        new DoublePowerSeries(coefficients)
                                .moveRight()
                                .plus(new DoublePowerSeries(new double[]{1.0}))
                );
    }

    @Override
    protected Direction distort(int x, int y) {
        double dx = x - centerX;
        double dy = y - centerY;
        double rSquared = dx*dx + dy*dy;
        double rCoefficient = distortionCoefficients.valueAt(rSquared);

        return new Direction(
                dx*rCoefficient + centerX,
                dy*rCoefficient + centerY
        );
    }

}
