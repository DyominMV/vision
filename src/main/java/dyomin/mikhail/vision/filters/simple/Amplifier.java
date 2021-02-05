package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.images.ReadableImage;

public class Amplifier<V extends Vector<V>> implements SimpleImageFilter<V,V> {
    public final double coefficient;

    public Amplifier(double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public V filter(int x, int y, ReadableImage<V> image) {
        return image.getPixel(x,y).amplify(coefficient);
    }
}
