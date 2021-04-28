package dyomin.mikhail.vision.filters.simple.singlepoint;

import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.function.Function;

public interface SinglePointFilter<V extends Vector<V>, U extends Vector<U>> extends SimpleImageFilter<V,U> {

    U filter(V value);

    @Override
    default U filter(int x, int y, ReadableImage<V> image){
        return filter(image.getPixel(x,y));
    }

    static <V extends Vector<V>, U extends Vector<U>> SinglePointFilter<V,U> of(Function<V,U> filter){
        return filter::apply;
    }
}
