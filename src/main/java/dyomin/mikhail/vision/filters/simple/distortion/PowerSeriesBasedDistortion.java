package dyomin.mikhail.vision.filters.simple.distortion;

import dyomin.mikhail.vision.math.powerseries2.TwoDoubleVariablePowerSeries;
import dyomin.mikhail.vision.math.powerseries2.TwoDoubleVariablePowerSeriesInversion;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.Vector;

public class PowerSeriesBasedDistortion<V extends Vector<V>> extends Distortion<V> {
    private static final int INVERSE_DISTORTION_COEFFICIENTS = 13;

    public final double centerX;
    public final double centerY;
    public final double scale;
    public final TwoDoubleVariablePowerSeries xPowerSeries;
    public final TwoDoubleVariablePowerSeries yPowerSeries;

    public PowerSeriesBasedDistortion(
            double centerX, double centerY, double scale,
            TwoDoubleVariablePowerSeries xPowerSeries, TwoDoubleVariablePowerSeries yPowerSeries
    ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.scale = scale;

        this.xPowerSeries = xPowerSeries.substitute(
                new TwoDoubleVariablePowerSeries(new double[][]{{1}}).moveRightX(),
                new TwoDoubleVariablePowerSeries(new double[][]{{1}}).moveRightY()
        );

        this.yPowerSeries = yPowerSeries.substitute(
                new TwoDoubleVariablePowerSeries(new double[][]{{1}}).moveRightX(),
                new TwoDoubleVariablePowerSeries(new double[][]{{1}}).moveRightY()
        );
    }


    @Override
    protected Direction distort(double x, double y) {
        double x0 = (x - centerX) / scale;
        double y0 = (y - centerY) / scale;

        return new Direction(
                xPowerSeries.valueAt(x0, y0) * scale + centerX,
                yPowerSeries.valueAt(x0, y0) * scale + centerY
        );
    }

    public PowerSeriesBasedDistortion<V> inverse() {
        TwoDoubleVariablePowerSeriesInversion inversion = new TwoDoubleVariablePowerSeriesInversion(
                xPowerSeries, yPowerSeries, 1, 1,
                INVERSE_DISTORTION_COEFFICIENTS, INVERSE_DISTORTION_COEFFICIENTS
        );
        return new PowerSeriesBasedDistortion<>(centerX, centerY, scale, inversion.outputX, inversion.outputY);
    }
}
