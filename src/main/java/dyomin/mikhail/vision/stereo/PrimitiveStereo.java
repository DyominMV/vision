package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrimitiveStereo<V extends Vector<V>> extends DsiBasedDisparityMap<V> {
    public PrimitiveStereo(ReadableImage<V> left, ReadableImage<V> right) {
        super((SimpleImageFilter<V, V>) (x, y, image) -> image.getPixel(x,y), left, right);
    }

    // just find every minimum
    @Override
    protected List<Disparity> getDisparities(List<V> leftSideRow, List<V> rightSideRow) {
        return IntStream.range(0, leftSideRow.size())
                .mapToObj(left -> {
                    V leftValue = leftSideRow.get(left);

                    int minRight = 0;
                    double minDifference = Double.MAX_VALUE;

                    for (int right = 0; right < rightSideRow.size(); right++) {
                        double difference = leftValue.minus(rightSideRow.get(right)).length();

                        if (difference < minDifference) {
                            minDifference = difference;
                            minRight = right;
                        }
                    }

                    return new Disparity(minRight - left);
                })
                .collect(Collectors.toList());
    }
}
