package dyomin.mikhail.vision.images;

import dyomin.mikhail.vision.vectors.Vector;

public abstract class EditableImage<V extends Vector<V>> extends ReadableImage<V> {

    protected abstract V getPixelBounded(int x, int y);

    protected abstract void setPixelBounded(int x, int y, V value);

    private int normalizeX(int x) {
        int mod = Math.abs(x) % (2 * getWidth());

        return mod >= getWidth() ?
                2 * getWidth() - mod - 1 :
                mod;
    }

    private int normalizeY(int y) {
        int mod = Math.abs(y) % (2 * getHeight());

        return mod >= getHeight() ?
                2 * getHeight() - mod - 1 :
                mod;
    }

    @Override
    public V getPixel(int x, int y) {
        return getPixelBounded(normalizeX(x), normalizeY(y));
    }

    public void setPixel(int x, int y, V value) {
        setPixelBounded(normalizeX(x), normalizeY(y), value);
    }

}
