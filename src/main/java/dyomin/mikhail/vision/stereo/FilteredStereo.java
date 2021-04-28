package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

public class FilteredStereo<V extends Vector<V>, U extends Vector<U>> implements Stereo<V> {

    private final Stereo<U> delegate;
    private final ImageFilter<V, U> filter;

    public FilteredStereo(Stereo<U> delegate, ImageFilter<V, U> filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    @Override
    public ReadableImage<Disparity> getDisparityMap(ReadableImage<V> left, ReadableImage<V> right) {
        return delegate.getDisparityMap(left.applyFilter(filter), right.applyFilter(filter));
    }
}
