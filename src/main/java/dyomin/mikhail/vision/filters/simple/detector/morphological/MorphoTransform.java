package dyomin.mikhail.vision.filters.simple.detector.morphological;

import dyomin.mikhail.vision.filters.simple.detector.Detector;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedBoolean;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MorphoTransform implements Detector<WrappedBoolean> {
    public enum TransformKind {
        DILATE, ERODE
    }

    private final TransformKind transformKind;
    private final int[] coordinates;

    public MorphoTransform(TransformKind transformKind, int... coordinates) {
        this.transformKind = transformKind;
        this.coordinates = coordinates;
    }

    @Override
    public boolean detect(int x, int y, ReadableImage<WrappedBoolean> image) {
        Stream<WrappedBoolean> data = IntStream
                .range(0, coordinates.length / 2)
                .mapToObj(i -> image.getPixel(x + coordinates[i * 2], y + coordinates[i * 2 + 1]));

        switch (transformKind) {
            case ERODE:
                return data.allMatch(b -> b.value);
            case DILATE:
                return data.anyMatch(b -> b.value);
        }

        return false;
    }

    public static MorphoTransform box(TransformKind transformKind) {
        return new MorphoTransform(transformKind,
                -1, -1, -1, 0, -1, 1,
                0, -1, 0, 0, 0, 1,
                1, -1, 1, 0, 1, 1
        );
    }
}
