package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class DsiBasedStereo<V extends Vector<V>>
        extends StereoBase<V> {
    protected <U extends Vector<U>> DsiBasedStereo(
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

        List<CompletableFuture<Void>> futures = IntStream.range(0, result.getHeight())
                .mapToObj(y -> CompletableFuture.runAsync(
                        () -> {
                            List<Disparity> disparities = getDisparities(
                                    left.getRow(y).collect(Collectors.toList()),
                                    right.getRow(y).collect(Collectors.toList())
                            );

                            for (int x = 0; x < result.getWidth(); x++) {
                                result.setPixel(x, y, disparities.get(x));
                            }
                        }
                ))
                .collect(Collectors.toList());
        
        futures.forEach(CompletableFuture::join);

        return result;
    }

    protected static final class IndexedVector<V extends Vector<V>> {
        public final int index;
        public final V value;

        public IndexedVector(int index, V value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IndexedVector<?> that = (IndexedVector<?>) o;
            return index == that.index && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, value);
        }
    }

    protected List<IndexedVector<V>> sortedList(List<V> list) {
        return IntStream
                .range(0, list.size())
                .mapToObj(i -> new IndexedVector<>(i, list.get(i)))
                .sorted(Comparator.comparingDouble(o -> o.value.length()))
                .collect(Collectors.toList());
    }
}
