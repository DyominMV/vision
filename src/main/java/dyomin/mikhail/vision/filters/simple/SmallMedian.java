package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.Comparator;
import java.util.stream.Stream;

public class SmallMedian<V extends Vector<V>> implements SimpleImageFilter<V, V> {
    @Override
    public V filter(int x, int y, ReadableImage<V> image) {
        return Stream
                .of(
                        image.getPixel(x - 1, y - 1),
                        image.getPixel(x, y - 1),
                        image.getPixel(x + 1, y - 1),
                        image.getPixel(x - 1, y),
                        image.getPixel(x, y),
                        image.getPixel(x + 1, y),
                        image.getPixel(x - 1, y + 1),
                        image.getPixel(x, y + 1),
                        image.getPixel(x + 1, y + 1)
                )
                .sorted(Comparator.comparingDouble(V::length))
                .skip(4)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Median filter has nothing to filter"));
    }
}
