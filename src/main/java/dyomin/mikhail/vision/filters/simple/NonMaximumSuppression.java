package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Direction;

public class NonMaximumSuppression implements SimpleImageFilter<Direction, Direction> {

    @Override
    public Direction filter(int x, int y, ReadableImage<Direction> image) {
        Direction base = new Direction(x, y);
        Direction gradient = image.getPixel(x, y);
        double baseValue = gradient.length();

        if (Double.MIN_NORMAL >= baseValue){
            return new Direction(0,0);
        }

        Direction gradientDirection = gradient.amplify(1 / baseValue);

        Direction point1 = base.plus(gradientDirection);
        double point1Value = image.getSubpixel(point1.x, point1.y).length();

        Direction point2 = base.minus(gradientDirection);
        double point2Value = image.getSubpixel(point2.x, point2.y).length();

        return (baseValue >= point1Value && baseValue >= point2Value) ?
                gradient :
                new Direction(0,0);
    }
}
