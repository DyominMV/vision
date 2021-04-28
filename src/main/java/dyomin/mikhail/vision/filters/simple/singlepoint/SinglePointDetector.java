package dyomin.mikhail.vision.filters.simple.singlepoint;

import dyomin.mikhail.vision.filters.simple.detector.Detector;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

public interface SinglePointDetector<V extends Vector<V>> extends Detector<V> {
    boolean detect(V value);

    @Override
    default boolean detect(int x, int y, ReadableImage<V> image){
        return detect(image.getPixel(x,y));
    }
}
