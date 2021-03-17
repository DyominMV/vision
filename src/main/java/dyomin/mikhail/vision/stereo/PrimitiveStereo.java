package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrimitiveStereo<V extends Vector<V>> extends DsiBasedStereo<V> {

    private final double metricThreshold;

    public PrimitiveStereo(ReadableImage<V> left, ReadableImage<V> right, double metricThreshold) {
        super((SimpleImageFilter<V, V>) (x, y, image) -> image.getPixel(x, y), left, right);
        this.metricThreshold = metricThreshold;
    }

    // greedy algorithm
    @Override
    protected List<Disparity> getDisparities(List<V> leftSideRow, List<V> rightSideRow) {
        LinkedList<IndexedVector<V>> rightSideList = IntStream.range(0, rightSideRow.size())
                .mapToObj(i -> new IndexedVector<>(i, rightSideRow.get(i)))
                .collect(Collectors.toCollection(LinkedList::new));

        return IntStream.range(0, leftSideRow.size()).mapToObj(i -> new IndexedVector<>(i, leftSideRow.get(i)))
                .map(lVector -> getClosestAndRemove(lVector, rightSideList))
                .collect(Collectors.toList());
    }

    private Disparity getClosestAndRemove(IndexedVector<V> leftVector, List<IndexedVector<V>> rightSideRow) {
        IndexedVector<V> result =
                rightSideRow.stream()
                        .min(Comparator.comparingDouble(iv -> iv.value.minus(leftVector.value).length()))
                        .orElse(new IndexedVector<>(0, leftVector.value));

        if (metricThreshold < result.value.minus(leftVector.value).length()) {
            return new Disparity(Double.NaN);
        }

        rightSideRow.remove(result);
        return new Disparity(result.index - leftVector.index);
    }
}
