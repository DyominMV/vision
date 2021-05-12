package dyomin.mikhail.vision.filters.simple.detector;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

public class BelowLengthThreshold<V extends Vector<V>> implements Detector<V> {
    private final double threshold;

    public BelowLengthThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean detect(int x, int y, ReadableImage<V> image) {
        return image.getPixel(x,y).length() <= threshold;
    }
}
