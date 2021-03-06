package dyomin.mikhail.vision.images;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.vectors.Vector;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class ReadableImage<V extends Vector<V>> {

    public abstract BufferedImage visualize();

    public abstract EditableImage<V> provideBuffer();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract V getPixel(int x, int y);

    /**
     * gets vector value via interpolation
     *
     * @param x first coordinate of vector's position
     * @param y second coordinate of vector's position
     * @return value somewhere between (x-0.5, y-0.5) and (x+0.5, y+0.5)
     */
    public V getSubpixel(double x, double y) {
        int x1 = (int) Math.floor(x);
        double xDiff = x - x1;
        int x2 = (int) Math.ceil(x);

        int y1 = (int) Math.floor(y);
        double yDiff = y - y1;
        int y2 = (int) Math.ceil(y);

        double topLeftSquare = xDiff * yDiff;
        double bottomLeftSquare = xDiff * (1 - yDiff);
        double bottomRightSquare = (1 - xDiff) * (1 - yDiff);
        double topRightSquare = (1 - xDiff) * yDiff;

        return getPixel(x1, y1).amplify(bottomRightSquare)
                .plus(getPixel(x1, y2).amplify(topRightSquare))
                .plus(getPixel(x2, y2).amplify(topLeftSquare))
                .plus(getPixel(x2, y1).amplify(bottomLeftSquare));
    }

    public double getCenterX() {
        return (getWidth() - 1.0) / 2;
    }

    public double getCenterY() {
        return (getHeight() - 1.0) / 2;
    }

    public <W extends Vector<W>, U extends Vector<U>> EditableImage<W> zipWith(
            ReadableImage<U> other,
            BiFunction<V, U, W> zipper
    ) {
        EditableImage<W> result = new MatrixImage<>(
                Math.max(this.getWidth(), other.getWidth()),
                Math.max(this.getHeight(), other.getHeight())
        );

        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                result.setPixel(x, y, zipper.apply(this.getPixel(x, y), other.getPixel(x, y)));
            }
        }

        return result;
    }

    public <U extends Vector<U>> EditableImage<U> applyFilter(ImageFilter<V, U> filter) {
        EditableImage<U> buffer = new MatrixImage<>(this.getWidth(), this.getHeight());
        filter.filter(this, buffer);
        return buffer;
    }

    public <U extends Vector<U>> EditableImage<U> applyFilter(ImageFilter<V, U> filter, Supplier<EditableImage<U>> bufferSupplier) {
        EditableImage<U> buffer = bufferSupplier.get();
        filter.filter(this, buffer);
        return buffer;
    }

    public EditableImage<V> applyFilters(List<ImageFilter<V, V>> filters) {
        EditableImage<V> b1 = provideBuffer();

        if (filters.isEmpty()) {
            return b1;
        }

        EditableImage<V> b2 = provideBuffer();

        filters.get(0).filter(this, b1);

        for (ImageFilter<V, V> filter : filters.stream().skip(1).collect(Collectors.toList())) {
            filter.filter(b1, b2);

            EditableImage<V> temp = b1;
            b1 = b2;
            b2 = temp;
        }

        return b1;
    }

    public Stream<V> getColumn(int x){
        return IntStream.range(0, getHeight()).mapToObj(
                y-> getPixel(x, y)
        );
    }

    public Stream<V> getRow(int y){
        return IntStream.range(0, getWidth()).mapToObj(
                x-> getPixel(x, y)
        );
    }
}
