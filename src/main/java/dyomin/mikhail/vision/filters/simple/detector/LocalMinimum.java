package dyomin.mikhail.vision.filters.simple.detector;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public class LocalMinimum implements Detector<WrappedDouble> {
    private final Direction direction;

    public LocalMinimum(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean detect(int x, int y, ReadableImage<WrappedDouble> image) {
        double here = image.getPixel(x, y).value;

        if (direction == Direction.HORIZONTAL){
            return (here <= image.getPixel(x+1,y).value) && (here <= image.getPixel(x-1,y).value);
        } else if (direction == Direction.VERTICAL){
            return (here <= image.getPixel(x,y+1).value) && (here <= image.getPixel(x,y-1).value);
        } else if (direction == Direction.DIAGONAL_L){
            return (here <= image.getPixel(x+1,y+1).value) && (here <= image.getPixel(x-1,y-1).value);
        } else if (direction == Direction.DIAGONAL_R){
            return (here <= image.getPixel(x+1,y-1).value) && (here <= image.getPixel(x-1,y+1).value);
        } else {
            return false;
        }
    }

    public enum Direction{
        VERTICAL, HORIZONTAL, DIAGONAL_L, DIAGONAL_R
    }
}
