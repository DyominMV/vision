package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

public interface Stereo<V extends Vector<V>> {
    ReadableImage<Disparity> getDisparityMap(ReadableImage<V> left, ReadableImage<V> right);

    default <U extends Vector<U>> Stereo<U> filtered(ImageFilter<U, V> filter) {
        return new FilteredStereo<>(this, filter);
    }
}
