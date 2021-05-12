package dyomin.mikhail.vision.filters.simple.detector;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public class GutterDetector implements Detector<WrappedDouble> {

    @Override
    public boolean detect(int x, int y, ReadableImage<WrappedDouble> image) {
        Direction alongGradient = new Direction(
                image.getPixel(x+1,y).minus(image.getPixel(x-1,y)).value,
                image.getPixel(x,y+1).minus(image.getPixel(x,y-1)).value
        )
                .normalize()
                .amplify(0.5);

        Direction normalToGradient = alongGradient
                .rotate(Math.PI / 2);

        double here = image.getPixel(x,y).value;

        double secondDerivativeAlongGradient =0
                + image.getSubpixel(x - alongGradient.x, y - alongGradient.y).value
                + image.getSubpixel(x + alongGradient.x, y + alongGradient.y).value
                - 2 * here;

        double secondDerivativeNormalToGradient =0
                + image.getSubpixel(x - normalToGradient.x, y - normalToGradient.y).value
                + image.getSubpixel(x + normalToGradient.x, y + normalToGradient.y).value
                - 2 * here;

        return secondDerivativeNormalToGradient >= 0 &&
                (Math.abs(secondDerivativeAlongGradient) <= Math.abs(secondDerivativeNormalToGradient));
    }
}
