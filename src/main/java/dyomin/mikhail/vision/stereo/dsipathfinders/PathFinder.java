package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public interface PathFinder {
    int[] findPath(ReadableImage<WrappedDouble> dsi);
}
