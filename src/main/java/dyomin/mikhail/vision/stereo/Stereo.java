package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public interface Stereo<V extends Vector<V>> {
    ReadableImage<Disparity> getDisparityMap(ReadableImage<V> left, ReadableImage<V> right);

    default MatrixImage<WrappedDouble> getDsi(ReadableImage<V> left, ReadableImage<V> right, int yPos) {
        MatrixImage<WrappedDouble> result = new MatrixImage<>(left.getWidth(), right.getWidth());

        for (int leftX = 0; leftX < left.getWidth(); leftX++) {
            for (int rightX = 0; rightX < right.getWidth(); rightX++) {
                //noinspection SuspiciousNameCombination
                result.setPixel(leftX, rightX, new WrappedDouble(
                                left.getPixel(leftX, yPos)
                                        .minus(right.getPixel(rightX, yPos))
                                        .length()
                        )
                );
            }
        }

        return result;
    }

    default <U extends Vector<U>> Stereo<U> filtered(ImageFilter<U,V> filter){
        return new FilteredStereo<>(this, filter);
    }
}
