package dyomin.mikhail.vision.filters;

import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.vectors.Vector;

public interface ImageFilter<V extends Vector<V>, U extends Vector<U>> {
    void filter(ReadableImage<V> image, EditableImage<U> output);

    default <V1 extends Vector<V1>> ImageFilter<V,V1> compose (ImageFilter<U,V1> other){
        return (image, output) -> {
            EditableImage<U> buffer = new MatrixImage<>(output.getWidth(), output.getHeight());
            ImageFilter.this.filter(image, buffer);
            other.filter(buffer, output);
        };
    }
}
