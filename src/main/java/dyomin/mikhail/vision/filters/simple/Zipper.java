package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.function.BiFunction;

public class Zipper<X extends Vector<X>, Y extends Vector<Y>, Z extends Vector<Z>> implements SimpleImageFilter<X, Z> {
    private final ReadableImage<Y> image;
    private final BiFunction<X,Y,Z> zipper;

    public Zipper(ReadableImage<Y> image, BiFunction<X, Y, Z> zipper) {
        this.image = image;
        this.zipper = zipper;
    }


    @Override
    public Z filter(int x, int y, ReadableImage<X> image) {
        return zipper.apply(image.getPixel(x,y), this.image.getPixel(x,y));
    }
}
