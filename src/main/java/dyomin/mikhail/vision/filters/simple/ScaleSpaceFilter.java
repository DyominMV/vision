package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.PackOfVectors;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScaleSpaceFilter<V extends Vector<V>> implements ImageFilter<V, PackOfVectors<V>> {

    private final List<PseudoGaussianBlur<V>> filters;

    public ScaleSpaceFilter(double... scales) {
        this.filters = Arrays.stream(scales)
                .mapToObj(scale -> new PseudoGaussianBlur<V>(scale, SystemOrder.FIVE))
                .collect(Collectors.toList());
    }

    @Override
    public void filter(ReadableImage<V> image, EditableImage<PackOfVectors<V>> output) {
        for (int x = 0; x < output.getWidth(); x++) {
            for (int y = 0; y < output.getHeight(); y++) {
                ArrayList<V> list = new ArrayList<>(filters.size());
                list.add(image.getPixel(x,y));
                output.setPixel(x, y, new PackOfVectors<>(list));
            }
        }

        filters.parallelStream().map(image::applyFilter).forEachOrdered(img -> {
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    output.getPixel(x, y).getVectors().add(img.getPixel(x, y));
                }
            }
        });
    }
}
