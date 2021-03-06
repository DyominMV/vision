package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.List;
import java.util.stream.Collectors;

public abstract class DsiBasedDisparityMap<V extends Vector<V>>
        extends LeftDisparityMap<V> {
    protected <U extends Vector<U>> DsiBasedDisparityMap(
            ImageFilter<U, V> filter,
            ReadableImage<U> left,
            ReadableImage<U> right
    ) {
        super(left.applyFilter(filter), right.applyFilter(filter));
    }

    protected abstract List<Disparity> getDisparities(List<V> leftSideRow, List<V> rightSideRow);

    @Override
    protected ReadableImage<Disparity> getDisparityMap(ReadableImage<V> left, ReadableImage<V> right) {
        EditableImage<Disparity> result = new MatrixImage<>(left.getWidth(), left.getHeight());

        for (int y = 0; y < result.getHeight(); y++) {
            List<Disparity> disparities = getDisparities(
                    left.getRow(y).collect(Collectors.toList()),
                    right.getRow(y).collect(Collectors.toList())
            );

            for (int x = 0; x < result.getWidth(); x++) {
                result.setPixel(x, y, disparities.get(x));
            }
        }

        return result;
    }
}
