package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.MagicFeature;
import dyomin.mikhail.vision.vectors.Vector;

public class MagicFilter<V extends Vector<V>> implements SimpleImageFilter<V, MagicFeature<V>> {
    @Override
    public MagicFeature<V> filter(int x, int y, ReadableImage<V> image) {
        return new MagicFeature<>(image.getPixel(x,y+1).minus(image.getPixel(x,y-1)), image.getPixel(x, y));
    }
}
