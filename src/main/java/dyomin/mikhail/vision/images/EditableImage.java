package dyomin.mikhail.vision.images;

import dyomin.mikhail.vision.vectors.Vector;

public abstract class EditableImage<V extends Vector<V>> extends ReadableImage<V> {

    protected abstract void setPixelBounded(int x, int y, V value);

    public void setPixel(int x, int y, V value) {
        setPixelBounded(normalizeX(x), normalizeY(y), value);
    }

}
