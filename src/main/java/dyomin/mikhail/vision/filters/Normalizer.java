package dyomin.mikhail.vision.filters;

import dyomin.mikhail.vision.filters.simple.singlepoint.Amplifier;
import dyomin.mikhail.vision.filters.simple.singlepoint.SinglePointFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.function.Function;
import java.util.stream.IntStream;

public class Normalizer<V extends Vector<V>> implements ImageFilter<V, V> {
    @Override
    public void filter(ReadableImage<V> image, EditableImage<V> output) {
        double length =
                IntStream.range(0, image.getWidth())
                        .mapToObj(image::getColumn)
                        .flatMap(Function.identity())
                        .mapToDouble(V::length)
                        .max()
                        .orElse(0);

        if (length == 0) {
            ((SinglePointFilter<V, V>) value -> null).filter(image, output);
        } else {
            new Amplifier<V>(1 / length).filter(image, output);
        }
    }
}
