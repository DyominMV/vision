package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

public class Threshold<V extends Vector<V>> implements SimpleImageFilter<V, V> {
    public final double lengthThreshold;

    public Threshold(double lengthThreshold) {
        if (lengthThreshold <= 0) throw new RuntimeException("threshold must be a positive value!");

        this.lengthThreshold = lengthThreshold;
    }

    @Override
    public V filter(int x, int y, ReadableImage<V> image) {
        V pixel = image.getPixel(x,y);

        return pixel.length() > lengthThreshold ?
                pixel :
                pixel.amplify(0);
    }
}
