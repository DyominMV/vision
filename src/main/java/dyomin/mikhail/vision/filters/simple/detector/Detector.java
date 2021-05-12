package dyomin.mikhail.vision.filters.simple.detector;

import dyomin.mikhail.vision.filters.ImageFilter;
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

    default Detector<V> and(Detector<V> other){
        return (x, y, image) -> this.detect(x,y,image) && other.detect(x,y,image);
    }

    default Detector<V> or(Detector<V> other){
        return (x, y, image) -> this.detect(x,y,image) || other.detect(x,y,image);
    }

    default Detector<V> not(Detector<V> other){
        return (x, y, image) -> !this.detect(x,y,image);
    }

    default <U extends Vector<U>> ImageFilter<V, U> toValues(U asFalse, U asTrue){
        return (SimpleImageFilter<V, U>) (x, y, image) -> Detector.this.detect(x,y,image) ? asTrue : asFalse;
    }
}
