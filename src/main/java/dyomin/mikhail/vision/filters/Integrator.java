package dyomin.mikhail.vision.filters;

import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

public class Integrator<V extends Vector<V>> implements ImageFilter<V,V> {
    @Override
    public void filter(ReadableImage<V> image, EditableImage<V> output) {
        output.setPixel(0, 0, image.getPixel(0, 0));

        for (int x = 1; x < image.getWidth(); x++) {
            output.setPixel(x, 0, image.getPixel(x, 0).plus(output.getPixel(x - 1, 0)));
        }

        for (int y = 1; y < image.getHeight(); y++) {
            output.setPixel(0, y, image.getPixel(0, y).plus(output.getPixel(0, y - 1)));
        }

        for (int x = 1; x < image.getWidth(); x++) {
            for (int y = 1; y < image.getHeight(); y++) {
                output.setPixel(x, y,
                        image.getPixel(x, y)
                                .plus(output.getPixel(x, y - 1))
                                .plus(output.getPixel(x - 1, y))
                                .minus(output.getPixel(x - 1, y - 1))
                );
            }
        }
    }
}
