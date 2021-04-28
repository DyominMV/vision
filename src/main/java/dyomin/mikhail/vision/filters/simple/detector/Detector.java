package dyomin.mikhail.vision.filters.simple.detector;

import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.vectors.WrappedBoolean;

public interface Detector<V extends Vector<V>> extends SimpleImageFilter<V, WrappedBoolean> {
    @Override
    default WrappedBoolean filter(int x, int y, ReadableImage<V> image){
        return new WrappedBoolean(detect(x,y,image));
    }

    boolean detect(int x, int y, ReadableImage<V> image);
}
