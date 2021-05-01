package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public interface Stereo<V extends Vector<V>> {
    ReadableImage<Disparity> getDisparityMap(ReadableImage<V> left, ReadableImage<V> right);

    ReadableImage<WrappedDouble> getDsi(ReadableImage<V> left, ReadableImage<V> right, int y);

    default <U extends Vector<U>> Stereo<U> filtered(ImageFilter<U, V> filter) {
        return new FilteredStereo<>(this, filter);
    }
}
