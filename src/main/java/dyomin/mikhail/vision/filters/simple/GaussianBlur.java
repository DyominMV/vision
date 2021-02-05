package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.Vector;

public class GaussianBlur<V extends Vector<V>> implements SimpleImageFilter<V, V> {
    public final int size;
    public final double sigma;

    private final Direction direction;
    private final double[] weights;

    public GaussianBlur(int size, double sigma, double directionRadians) {
        this.size = size;
        this.weights = new double[size];
        this.sigma = sigma;
        this.direction = new Direction(Math.cos(directionRadians), Math.sin(directionRadians));

        double sum = 0;

        for (int i = 0; i < size; i++) {
            weights[i] = Math.exp(-(double) i * i / 2 / sigma / sigma);
            sum += weights[i];
        }

        sum = sum*2 - weights[0];

        for (int i = 0; i< size; i++){
            weights[i] /= sum;
        }
    }

    @Override
    public V filter(int x, int y, ReadableImage<V> image) {
        V result = image.getPixel(x,y).amplify(weights[0]);

        for (int i = 1; i < size; i++) {
            result = result
                    .plus(image.getSubpixel(x+direction.x*i, y+direction.y*i).amplify(weights[i]))
                    .plus(image.getSubpixel(x-direction.x*i, y-direction.y*i).amplify(weights[i]));
        }

        return result;
    }
}
