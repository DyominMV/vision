package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.images.ReadableImage;

public class Gradient implements SimpleImageFilter<RGB, Direction> {
    @Override
    public Direction filter(int x, int y, ReadableImage<RGB> image) {
        RGB vertical = image.getPixel(x, y + 1)
                .minus(image.getPixel(x, y - 1));

        RGB horizontal = image.getPixel(x - 1, y)
                .minus(image.getPixel(x + 1, y));

        return new Direction(
                vertical.length() / RGB.MAX_LENGTH * (vertical.activateToBoolean() ? 1 : -1),
                horizontal.length() / RGB.MAX_LENGTH * (horizontal.activateToBoolean() ? 1 : -1)
        );
    }
}
