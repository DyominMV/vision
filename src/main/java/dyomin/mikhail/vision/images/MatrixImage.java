package dyomin.mikhail.vision.images;

import dyomin.mikhail.vision.vectors.Vector;

import java.awt.image.BufferedImage;

public class MatrixImage<T extends Vector<T>> extends EditableImage<T> {

    private final Object[][] data;

    public MatrixImage(int width, int height) {
        this.data = new Object[width][height];
    }

    @Override
    public int getWidth() {
        return data.length;
    }

    @Override
    public int getHeight() {
        return data[0].length;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T getPixelBounded(int x, int y) {
        return (T) data[x][y];
    }

    @Override
    protected void setPixelBounded(int x, int y, T value) {
        data[x][y] = value;
    }

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
    public EditableImage<T> provideBuffer() {
        return new MatrixImage<T>(this.getWidth(), this.getHeight());
    }
}
