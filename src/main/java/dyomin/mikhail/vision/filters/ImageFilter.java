package dyomin.mikhail.vision.filters;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.vectors.Vector;

public interface ImageFilter<V extends Vector<V>, U extends Vector<U>> {
    void filter(ReadableImage<V> image, EditableImage<U> output);
}
