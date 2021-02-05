package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.images.ReadableImage;

public interface SimpleImageFilter<V extends Vector<V>, U extends Vector<U>> extends ImageFilter<V,U> {
    U filter(int x, int y, ReadableImage<V> image);

    @Override
    default void filter(ReadableImage<V> image, EditableImage<U> output){
        for (int x = 0; x< output.getWidth(); x++){
            for (int y = 0; y< output.getHeight(); y++){
                output.setPixel(x,y,filter(x,y,image));
            }
        }
    }
}
