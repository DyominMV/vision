package dyomin.mikhail.vision.filters.simple.detector;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Direction;

public class GutterDetector implements Detector<Direction> {

    private final double maxAngleDifference;

    public GutterDetector(double maxAngleDifference) {
        this.maxAngleDifference = Math.abs(maxAngleDifference);
    }

    @Override
    public boolean detect(int x, int y, ReadableImage<Direction> image) {
        Direction gradientHere = image.getPixel(x, y).normalize();

        if (0 >= gradientHere.length()) {
            return true;
        }

        Direction perpendicular = gradientHere.rotate(Math.PI / 2);

        Direction pointHere = new Direction(x, y);
        Direction point1 = pointHere.minus(perpendicular);
        Direction point2 = pointHere.plus(perpendicular);

        Direction gradientSum = image.getSubpixel(point1.x, point1.y)
                .plus(image.getSubpixel(point2.x, point2.y))
                .normalize();

        if (0 >= gradientSum.length()){
            return true;
        }

        return Math.abs(gradientSum.angle() - gradientHere.angle()) < maxAngleDifference;
    }
}
