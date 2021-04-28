package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface LineByLineStereo<V extends Vector<V>> extends Stereo<V> {

    List<Disparity> getDisparities(List<V> left, List<V> right);

    @Override
    default ReadableImage<Disparity> getDisparityMap(ReadableImage<V> left, ReadableImage<V> right) {
        EditableImage<Disparity> result = new MatrixImage<>(left.getWidth(), left.getHeight());

        IntStream.range(0, result.getHeight()).parallel().forEach(y-> {
            List<Disparity> disparities = getDisparities(
                    left.getRow(y).collect(Collectors.toList()),
                    right.getRow(y).collect(Collectors.toList())
            );

            IntStream.range(0, result.getWidth()).parallel().forEach(x->
                    result.setPixel(x, y, disparities.get(x))
            );
        });

        return result;
    }
}
