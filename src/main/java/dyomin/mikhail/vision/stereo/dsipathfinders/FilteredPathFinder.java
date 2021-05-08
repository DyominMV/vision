package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public class FilteredPathFinder implements PathFinder {
    private final PathFinder delegate;
    private final ImageFilter<WrappedDouble, WrappedDouble> filter;

    public FilteredPathFinder(PathFinder delegate, ImageFilter<WrappedDouble, WrappedDouble> filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    @Override
    public int[] findPath(ReadableImage<WrappedDouble> dsi) {
        return delegate.findPath(dsi.applyFilter(filter));
    }
}
