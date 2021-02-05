package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.images.ReadableImage;

public class SobelFilter implements SimpleImageFilter<RGB, RGB> {

    @Override
    public RGB filter(int x, int y, ReadableImage<RGB> image) {
        RGB vertical = image.getPixel(x,y+1).amplify(2)
                .plus(image.getPixel(x-1, y+1))
                .plus(image.getPixel(x+1, y+1))
                .minus(image.getPixel(x,y-1).amplify(2))
                .minus(image.getPixel(x-1, y-1))
                .minus(image.getPixel(x+1, y-1));

        RGB horizontal = image.getPixel(x-1,y).amplify(2)
                .plus(image.getPixel(x-1, y+1))
                .plus(image.getPixel(x-1, y-1))
                .minus(image.getPixel(x+1,y).amplify(2))
                .minus(image.getPixel(x+1, y+1))
                .minus(image.getPixel(x+1, y-1));

        return new RGB(
                vertical.length()/RGB.MAX_LENGTH*(vertical.activateToBoolean() ? 1 : -1),
                horizontal.length()/RGB.MAX_LENGTH*(horizontal.activateToBoolean() ? 1 : -1),
                0
        );
    }
}
