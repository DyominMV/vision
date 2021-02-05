package dyomin.mikhail.vision.images;

import dyomin.mikhail.vision.vectors.Vector;

import java.awt.image.BufferedImage;

public abstract class ReadableImage<V extends Vector<V>> {

    public abstract BufferedImage visualize();

    public abstract EditableImage<V> provideBuffer();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract V getPixel(int x, int y);

    /**
     * gets vector value via interpolation
     *
     * @param x first coordinate of vector's position
     * @param y second coordinate of vector's position
     * @return value somewhere between (x-0.5, y-0.5) and (x+0.5, y+0.5)
     */
    public V getSubpixel(double x, double y) {
        int x1 = (int) Math.floor(x);
        double xDiff = x - x1;
        int x2 = (int) Math.ceil(x);

        int y1 = (int) Math.floor(y);
        double yDiff = y - y1;
        int y2 = (int) Math.ceil(y);

        double topLeftSquare = xDiff * yDiff;
        double bottomLeftSquare = xDiff * (1 - yDiff);
        double bottomRightSquare = (1 - xDiff) * (1 - yDiff);
        double topRightSquare = (1 - xDiff) * yDiff;

        return getPixel(x1, y1).amplify(bottomRightSquare)
                .plus(getPixel(x1, y2).amplify(topRightSquare))
                .plus(getPixel(x2, y2).amplify(topLeftSquare))
                .plus(getPixel(x2, y1).amplify(bottomLeftSquare));
    }

    public double getCenterX() {
        return (getWidth() - 1.0) / 2;
    }

    public double getCenterY() {
        return (getHeight() - 1.0) / 2;
    }

}
