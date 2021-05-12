package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.filters.simple.detector.Detector;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public class DetectedPathFinder implements PathFinder{
    private final BoolDsiPathFinder delegate;
    private final Detector<WrappedDouble> detector;

    public DetectedPathFinder(BoolDsiPathFinder delegate, Detector<WrappedDouble> detector) {
        this.delegate = delegate;
        this.detector = detector;
    }

    @Override
    public int[] findPath(ReadableImage<WrappedDouble> dsi) {
        return delegate.findPath(dsi.applyFilter(detector));
    }
}
