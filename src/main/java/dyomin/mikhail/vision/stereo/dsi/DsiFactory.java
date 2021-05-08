package dyomin.mikhail.vision.stereo.dsi;

import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

public class DsiFactory<V extends Vector<V>> {
    private final ToDoubleBiFunction<V,V> metric;

    public DsiFactory(ToDoubleBiFunction<V, V> metric) {
        this.metric = metric;
    }

    public ReadableImage<WrappedDouble> getDsi(List<V> left, List<V> right){
        return new ReadableImage<WrappedDouble>() {
            @Override
            public BufferedImage visualize() {
                RgbImage image = new RgbImage(getWidth(), getHeight());

                for (int x = 0; x < getWidth(); x++) {
                    for (int y = 0; y < getHeight(); y++) {
                        image.setPixel(x, y, this.getPixel(x, y).visualize());
                    }
                }

                return image.visualize();
            }

            @Override
            public EditableImage<WrappedDouble> provideBuffer() {
                return new MatrixImage<>(left.size(), right.size());
            }

            @Override
            public int getWidth() {
                return left.size();
            }

            @Override
            public int getHeight() {
                return right.size();
            }

            @Override
            public WrappedDouble getPixel(int x, int y) {
                return new WrappedDouble(metric.applyAsDouble(left.get(x), right.get(y)));
            }
        };
    }
}
