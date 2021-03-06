package dyomin.mikhail.vision.filters.simple.distortion;

import dyomin.mikhail.vision.math.powerseries.DoublePowerSeries;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.Arrays;
import java.util.stream.DoubleStream;

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
        this.radialPartCoefficients =
                new DoublePowerSeries(
                        Arrays.stream(radialPartCoefficients)
                                .flatMap(d -> DoubleStream.of(0, d))
                                .toArray()
                )
                        .moveRight()
                        .plus(new DoublePowerSeries(1.0));
    }


    @Override
    protected Direction distort(double x, double y) {
        double dx = x - centerX;
        double dy = y - centerY;
        double r = Math.sqrt(dx * dx + dy * dy);
        double rCoefficient = radialPartCoefficients.valueAt(r);

        return new Direction(
                dx + rCoefficient * (
                        tangentialCoefficient2 * (r*r + 2 * dx * dx) +
                                tangentialCoefficient1 * 2 * dx * dy
                ) + centerX,
                dy + rCoefficient * (
                        tangentialCoefficient1 * (r*r + 2 * dy * dy) +
                                tangentialCoefficient2 * 2 * dx * dy
                ) + centerY
        );
    }
}
