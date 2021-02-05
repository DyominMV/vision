package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

/**
 * Apply only after integrating!!!
 */
public class BoxBlur<V extends Vector<V>> implements SimpleImageFilter<V, V> {
    public final int size;

    public BoxBlur(int size) {
        this.size = size;
    }

    @Override
    public V filter(int x, int y, ReadableImage<V> image) {
        return image.getPixel(x - size - 1, y - size - 1)
                .plus(image.getPixel(x + size, y + size))
                .minus(image.getPixel(x + size, y - size - 1))
                .minus(image.getPixel(x - size - 1, y + size))
                .amplify(Math.pow(1.0 / (2 * size + 1), 2));
    }
}
