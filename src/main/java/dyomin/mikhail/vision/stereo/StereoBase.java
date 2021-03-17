package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class StereoBase<V extends Vector<V>> {
    private ReadableImage<Disparity> disparityMap;

    private ReadableImage<V> left;
    private ReadableImage<V> right;

    public synchronized ReadableImage<Disparity> getDisparityMap() {
        if (null != disparityMap){
            return disparityMap;
        }

        this.disparityMap = getDisparityMap(left, right);

        left = null;
        right = null;

        return disparityMap;
    }

    protected StereoBase(ReadableImage<V> left, ReadableImage<V> right) {
        this.left = left;
        this.right = right;
    }

    protected abstract ReadableImage<Disparity> getDisparityMap(ReadableImage<V> left, ReadableImage<V> right);

    public double getError(double nullWeight, ReadableImage<Disparity> sample) {
        return IntStream.range(0, sample.getWidth())
                .mapToObj(
                        x -> IntStream.range(0, sample.getHeight())
                                .mapToObj(
                                        y -> disparityMap.getPixel(x, y).minus(sample.getPixel(x, y))
                                )
                )
                .flatMap(Function.identity())
                .mapToDouble(disparity -> disparity.isNull() ? nullWeight : disparity.value)
                .sum();
    }
}
