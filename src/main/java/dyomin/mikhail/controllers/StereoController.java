package dyomin.mikhail.controllers;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.filters.simple.ScaleSpaceFilter;
import dyomin.mikhail.vision.filters.simple.singlepoint.Amplifier;
import dyomin.mikhail.vision.filters.simple.singlepoint.SinglePointDetector;
import dyomin.mikhail.vision.filters.simple.singlepoint.SinglePointFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.math.dsisolvers.HungarianSolver;
import dyomin.mikhail.vision.stereo.DsiStereo;
import dyomin.mikhail.vision.stereo.FilteredStereo;
import dyomin.mikhail.vision.stereo.Stereo;
import dyomin.mikhail.vision.vectors.PackOfVectors;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.WrappedDouble;

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

        Stereo<RGB> stereoBase = //new DsiStereo<>(new HungarianSolver());
                new DsiStereo<PackOfVectors<RGB>>(new HungarianSolver())
                .filtered(new ScaleSpaceFilter<>(2,3,4,5,6,7,8,9,10));

        stereoBase.getDsi(left, right, 200)
                .applyFilter((SinglePointDetector<WrappedDouble>) d -> d.value <= 0.2)
                .toRgbImage()
                .writeToFile(new File("/tmp/babyD.png"));

        stereoBase.getDisparityMap(left.scale(0.4), right.scale(0.4))
                .applyFilter(new Amplifier<>(0.1))
                .toRgbImage()
                .writeToFile(new File("/tmp/babyDmap.png"));

        System.out.println(System.currentTimeMillis() - timestart);
    }
}
