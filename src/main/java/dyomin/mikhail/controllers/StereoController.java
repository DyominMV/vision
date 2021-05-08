package dyomin.mikhail.controllers;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.ScaleSpaceFilter;
import dyomin.mikhail.vision.filters.simple.singlepoint.Amplifier;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.stereo.DsiStereo;
import dyomin.mikhail.vision.stereo.Stereo;
import dyomin.mikhail.vision.stereo.dsipathfinders.SimpleGraphPathFinder;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.Vector;

import java.io.File;
import java.io.IOException;

public class StereoController {

    private static <U extends Vector<U>> void handleFilter(
            ReadableImage<RGB> left,
            ReadableImage<RGB> right,
            ImageFilter<RGB, U> filter,
            String filterName
    ) throws IOException {
        Stereo<RGB> stereoBase =
                new DsiStereo<U>(
                        new SimpleGraphPathFinder(),
                        (p1, p2) -> p1.minus(p2).length()
                )
                        .filtered(filter);

        stereoBase.getDisparityMap(left.scale(0.5), right.scale(0.5))
                .applyFilter(new Amplifier<>(0.25))
                .toRgbImage()
                .writeToFile(new File("/tmp/babyDmap_" + filterName + ".png"));
    }

    public static void main(String[] args) throws IOException {
        long timestart = System.currentTimeMillis();

        ReadableImage<RGB> left = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyL.png").getFile()
        ));

        ReadableImage<RGB> right = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyR.png").getFile()
        ));

        handleFilter(left, right, new ScaleSpaceFilter<>(2,4,8,16,32,64), "2,4,8,16,32,64");
        handleFilter(left, right, new ScaleSpaceFilter<>(), "");
        handleFilter(left, right, new PseudoGaussianBlur<>(3, SystemOrder.THREE), "3");

        System.out.println(System.currentTimeMillis() - timestart);
    }
}
