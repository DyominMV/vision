package dyomin.mikhail.vision.images;

import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.Vector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RgbImage extends EditableImage<RGB> {
    private final BufferedImage bufferedImage;

    public RgbImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public RgbImage(int width, int height) {
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    }

    @Override
    public int getWidth() {
        return bufferedImage.getWidth();
    }

    @Override
    public int getHeight() {
        return bufferedImage.getHeight();
    }

    @Override
    protected RGB getPixelBounded(int x, int y) {
        return new RGB(bufferedImage.getRGB(x, y));
    }

    @Override
    protected void setPixelBounded(int x, int y, RGB value) {
        bufferedImage.setRGB(x, y, value.encode());
    }

    @Override
    public BufferedImage visualize() {
        return bufferedImage;
    }

    @Override
    public EditableImage<RGB> provideBuffer() {
        return new RgbImage(this.getWidth(), this.getHeight());
    }

    public MatrixImage<RGB> toMatrixImage() {
        MatrixImage<RGB> result = new MatrixImage<>(this.getWidth(), this.getHeight());

        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                result.setPixel(x, y, this.getPixel(x, y));
            }
        }

        return result;
    }

    public static <V extends Vector<V>> RgbImage fromSpecialImage(ReadableImage<V> editableImage) {
        RgbImage result = new RgbImage(editableImage.getWidth(), editableImage.getHeight());

        for (int x = 0; x < editableImage.getWidth(); x++) {
            for (int y = 0; y < editableImage.getHeight(); y++) {
                result.setPixel(x, y, editableImage.getPixel(x, y).visualize());
            }
        }

        return result;
    }

    public static RgbImage loadFromFile(File file) throws IOException {
        return new RgbImage(ImageIO.read(file));
    }

    public void writeToFile(File file) throws IOException {
        ImageIO.write(this.bufferedImage, "png", file);
    }

}
