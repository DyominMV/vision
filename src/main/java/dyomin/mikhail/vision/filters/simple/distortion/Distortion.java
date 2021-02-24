package dyomin.mikhail.vision.filters.simple.distortion;

import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.function.BiFunction;

public abstract class Distortion<V extends Vector<V>> implements SimpleImageFilter<V, V> {
    protected abstract Direction distort(double x, double y);

    @Override
    public final V filter(int x, int y, ReadableImage<V> image) {
        Direction point = distort(x, y);
        return image.getSubpixel(point.x, point.y);
    }

    public MatrixImage<Direction> precalculate(int width, int height) {
        MatrixImage<Direction> result = new MatrixImage<>(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result.setPixel(x, y, distort(x, y));
            }
        }

        return result;
    }

    public static <V extends Vector<V>> Distortion<V> withFunc(BiFunction<Double, Double, Direction> distorter) {
        return new Distortion<V>() {
            @Override
            protected Direction distort(double x, double y) {
                return distorter.apply(x, y);
            }
        };
    }

    public static <V extends Vector<V>> Distortion<V> precalculated(MatrixImage<Direction> distorter) {
        return new Distortion<V>() {
            @Override
            protected Direction distort(double x, double y) {
                return distorter.getSubpixel(x, y);
            }
        };
    }
}
