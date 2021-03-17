package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.images.ReadableImage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface SimpleImageFilter<V extends Vector<V>, U extends Vector<U>> extends ImageFilter<V, U> {
    U filter(int x, int y, ReadableImage<V> image);

    @Override
    default void filter(ReadableImage<V> image, EditableImage<U> output) {
        List<CompletableFuture<Void>> futures =
                IntStream.range(0, output.getWidth()).mapToObj(x ->
                        CompletableFuture.runAsync(() -> {
                            for (int y = 0; y < output.getHeight(); y++) {
                                output.setPixel(x, y, filter(x, y, image));
                            }
                        })
                ).collect(Collectors.toList());

        futures.forEach(CompletableFuture::join);
    }
}
