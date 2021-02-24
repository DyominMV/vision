package dyomin.mikhail.vision.filters.simple.distortion;

import dyomin.mikhail.vision.math.powerseries.DoublePowerSeries;
import dyomin.mikhail.vision.math.powerseries2.TwoDoubleVariablePowerSeries;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class BrownConradyDistortion<V extends Vector<V>> extends PowerSeriesBasedDistortion<V> {
    private static final TwoDoubleVariablePowerSeries R_SQUARED =
            new TwoDoubleVariablePowerSeries(new double[][]{
                    {0, 0, 1},
                    {},
                    {1}
            });

    public BrownConradyDistortion(
            double centerX, double centerY, double scale,
            double[] ks,
            double p1, double p2, double[] ps
    ) {
        super(centerX, centerY, scale, xPowerSeries(ks, p1, p2, ps), yPowerSeries(ks, p1, p2, ps));
    }

    private static TwoDoubleVariablePowerSeries seriesOfRSquared(double[] coefficients) {
        return R_SQUARED.substituteInto(
                new DoublePowerSeries(DoubleStream.concat(
                        DoubleStream.of(1),
                        Arrays.stream(coefficients)).toArray()
                )
        );
    }

    private static TwoDoubleVariablePowerSeries xPowerSeries(double[] ks, double p1, double p2, double[] ps) {
        TwoDoubleVariablePowerSeries radial = seriesOfRSquared(ks);
        TwoDoubleVariablePowerSeries tangentialBasic = seriesOfRSquared(ps);
        TwoDoubleVariablePowerSeries tangential = new TwoDoubleVariablePowerSeries(new double[][]{
                {0, 0, p1},
                {0, 2 * p2},
                {3 * p1}
        });

        return radial.moveRightX().plus(tangential.multiply(tangentialBasic));
    }

    private static TwoDoubleVariablePowerSeries yPowerSeries(double[] ks, double p1, double p2, double[] ps) {
        TwoDoubleVariablePowerSeries radial = seriesOfRSquared(ks);
        TwoDoubleVariablePowerSeries tangentialBasic = seriesOfRSquared(ps);
        TwoDoubleVariablePowerSeries tangential = new TwoDoubleVariablePowerSeries(new double[][]{
                {0, 0, 3 * p2},
                {0, 2 * p1},
                {p2}
        });

        return radial.moveRightY().plus(tangential.multiply(tangentialBasic));
    }
}
