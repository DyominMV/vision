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
import dyomin.mikhail.vision.stereo.dsipathfinders.FilteredPathFinder;
import dyomin.mikhail.vision.stereo.dsipathfinders.SimpleGraphPathFinder;
import dyomin.mikhail.vision.vectors.PackOfVectors;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.Vector;

import java.io.File;
import java.io.IOException;

public class StereoController {

    public static void main(String[] args) throws IOException {
        long timestart = System.currentTimeMillis();

        ReadableImage<RGB> left = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyL.png").getFile()
        ));

        ReadableImage<RGB> right = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyR.png").getFile()
        ));

        Stereo<RGB> stereoBase =
                new DsiStereo<PackOfVectors<RGB>>(
                        new FilteredPathFinder(
                                new SimpleGraphPathFinder(),
                                new PseudoGaussianBlur<>(3, SystemOrder.FIVE)
                        ),
                        (p1, p2) -> p1.minus(p2).length()
                )
                        .filtered(new ScaleSpaceFilter<>(2,3,4,5));

        stereoBase.getDisparityMap(left.scale(0.5), right.scale(0.5))
                .applyFilter(new Amplifier<>(0.25))
                .toRgbImage()
                .writeToFile(new File("/tmp/babyDmap_filtered.png"));

        System.out.println(System.currentTimeMillis() - timestart);
    }
}
