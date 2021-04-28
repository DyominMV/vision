package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.stream.IntStream;

public interface SimpleImageFilter<V extends Vector<V>, U extends Vector<U>> extends ImageFilter<V, U> {
    U filter(int x, int y, ReadableImage<V> image);

    @Override
    default void filter(ReadableImage<V> image, EditableImage<U> output) {
        IntStream.range(0, output.getWidth()).parallel().forEach(x -> {
                    for (int y = 0; y < output.getHeight(); y++) {
                        output.setPixel(x, y, filter(x, y, image));
                    }
                }
        );
    }
}
