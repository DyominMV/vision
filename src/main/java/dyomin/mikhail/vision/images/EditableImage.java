package dyomin.mikhail.vision.images;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class EditableImage<V extends Vector<V>> extends ReadableImage<V> {

    protected abstract V getPixelBounded(int x, int y);

    protected abstract void setPixelBounded(int x, int y, V value);

    private int normalizeX(int x) {
        int mod = Math.abs(x) % (2 * getWidth());

        return mod >= getWidth() ?
                2 * getWidth() - mod - 1 :
                mod;
    }

    private int normalizeY(int y) {
        int mod = Math.abs(y) % (2 * getHeight());

        return mod >= getHeight() ?
                2 * getHeight() - mod - 1 :
                mod;
    }

    @Override
    public V getPixel(int x, int y) {
        return getPixelBounded(normalizeX(x), normalizeY(y));
    }

    public void setPixel(int x, int y, V value) {
        setPixelBounded(normalizeX(x), normalizeY(y), value);
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

    public <W extends Vector<W>, U extends Vector<U>> EditableImage<W> zipWith(
            EditableImage<U> other,
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

    public <U extends Vector<U>> EditableImage<U> applyFilter(ImageFilter<V, U> filter, Supplier<EditableImage<U>> bufferSupplier) {
        EditableImage<U> buffer = bufferSupplier.get();
        filter.filter(this, buffer);
        return buffer;
    }

    public <U extends Vector<U>> EditableImage<U> applyFilter(ImageFilter<V, U> filter) {
        EditableImage<U> buffer = new MatrixImage<>(this.getWidth(), this.getHeight());
        filter.filter(this, buffer);
        return buffer;
    }

}
