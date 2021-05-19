package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.PackOfVectors;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.ArrayList;

public class VerticalWindow<V extends Vector<V>> implements SimpleImageFilter<V, PackOfVectors<V>> {

    private final int radius;

    public VerticalWindow(int radius) {
        this.radius = radius;
    }

    @Override
    public PackOfVectors<V> filter(int x, int y, ReadableImage<V> image) {
        ArrayList<V> result = new ArrayList<>(2*radius+1);
        result.add(image.getPixel(x, y));

        for (int i=1; i<= radius; i++){
            result.add(image.getPixel(x, y+radius));
            result.add(image.getPixel(x, y-radius));
        }

        return new PackOfVectors<>(result);
    }
}
