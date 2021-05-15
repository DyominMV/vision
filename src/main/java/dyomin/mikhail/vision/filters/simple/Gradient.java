package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public class Gradient implements SimpleImageFilter<WrappedDouble, Direction> {
    @Override
    public Direction filter(int x, int y, ReadableImage<WrappedDouble> image) {
        WrappedDouble vertical = image.getPixel(x, y + 1)
                .minus(image.getPixel(x, y - 1)).amplify(0.5);

        WrappedDouble horizontal = image.getPixel(x + 1, y)
                .minus(image.getPixel(x - 1, y)).amplify(0.5);

        return new Direction(horizontal.value, vertical.value);
    }
}
