package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.math.powerseries.DoublePowerSeries;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class RadialDistortion<V extends Vector<V>> extends Distortion<V> {
    private static final int INVERSE_DISTORTION_COEFFICIENT_GROUPS = 3;

    private final DoublePowerSeries distortionCoefficients;
    private final double centerX;
    private final double centerY;

    private RadialDistortion(double centerX, double centerY, DoublePowerSeries distortionCoefficients) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.distortionCoefficients = distortionCoefficients;
    }

    public RadialDistortion(double centerX, double centerY, double... coefficients) {
        this(
                centerX,
                centerY,
                new DoublePowerSeries(
                        Arrays.stream(coefficients)
                                .flatMap(d -> DoubleStream.of(0, d))
                                .toArray()
                )
                        .moveRight()
                        .plus(new DoublePowerSeries(1.0))
        );
    }

    @Override
    protected Direction distort(double x, double y) {
        double dx = x - centerX;
        double dy = y - centerY;
        double r = Math.sqrt(dx * dx + dy * dy);
        double rCoefficient = distortionCoefficients.valueAt(r);

        return new Direction(
                dx * rCoefficient + centerX,
                dy * rCoefficient + centerY
        );
    }

    public RadialDistortion<V> inverseDistortion() {
        return new RadialDistortion<>(centerX, centerY,
                distortionCoefficients.moveRight().compInverse((int)
                        distortionCoefficients.getDoubleCoefficients().count() *
                        INVERSE_DISTORTION_COEFFICIENT_GROUPS
                ).moveLeft()
        );
    }
}
