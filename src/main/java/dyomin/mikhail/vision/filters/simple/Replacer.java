package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.filters.simple.detector.Detector;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

public class Replacer<V extends Vector<V>> implements SimpleImageFilter<V,V> {
    private final Detector<V> predicate;
    private final V replacement;

    public Replacer(Detector<V> predicate, V replacement) {
        this.predicate = predicate;
        this.replacement = replacement;
    }

    @Override
    public V filter(int x, int y, ReadableImage<V> image) {
        return predicate.detect(x,y,image)
                ? replacement
                : image.getPixel(x,y);
    }
}
