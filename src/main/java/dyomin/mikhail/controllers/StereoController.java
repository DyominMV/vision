package dyomin.mikhail.controllers;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.GaussianBlur;
import dyomin.mikhail.vision.filters.simple.ScaleSpaceFilter;
import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.filters.simple.singlepoint.Amplifier;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.math.dsisolvers.DiagAmplifierSolver;
import dyomin.mikhail.vision.math.dsisolvers.HungarianSolver;
import dyomin.mikhail.vision.math.dsisolvers.SimpleGraphSolver;
import dyomin.mikhail.vision.math.dsisolvers.SolverWithOcclusions;
import dyomin.mikhail.vision.stereo.DsiStereo;
import dyomin.mikhail.vision.stereo.Stereo;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.io.File;
import java.io.IOException;

public class StereoController {

    private static <U extends Vector<U>> void handleFilter(
            ReadableImage<RGB> left,
            ReadableImage<RGB> right,
            ImageFilter<RGB, U> filter,
            String filterName
    ) throws IOException {
        Stereo<RGB> stereoBase = // new DsiStereo<>(new SimpleGraphSolver(), (p1, p2) -> p1.minus(p2).length());
                new DsiStereo<U>(
                        //new SolverWithOcclusions(i -> (i - 1) * (i - 1) / 10.0),
                        new DiagAmplifierSolver(new SolverWithOcclusions(i-> (i-1)*(i-1)/10.0)),
                        //new DiagAmplifierSolver(new SimpleGraphSolver()),
                        //new HungarianSolver(),
                        //new SimpleGraphSolver(),
                        (p1, p2) -> p1.minus(p2).length()
                )
                        .filtered(filter);

        stereoBase.getDisparityMap(left.scale(0.5), right.scale(0.5))
                .applyFilter(new Amplifier<>(0.25))
                .toRgbImage()
                .writeToFile(new File("/tmp/babyDmap_" + filterName + ".png"));

        stereoBase.getDsi(left, right, 200)
                .toRgbImage()
                .writeToFile(new File("/tmp/babyD_"+ filterName + ".png"));
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
        handleFilter(left, right, new ScaleSpaceFilter<>(), "1");
        handleFilter(left, right, new ScaleSpaceFilter<>(10,20,40), "10,20,40");
//        handleFilter(left, right, new ScaleSpaceFilter<>(2), "2");
//        handleFilter(left, right, new ScaleSpaceFilter<>(2, 4), "2_4");
//        handleFilter(left, right, new ScaleSpaceFilter<>(2, 4, 8), "2_4_8");
//        handleFilter(left, right, new ScaleSpaceFilter<>(2, 4, 8, 16), "2_4_8_16");
//        handleFilter(left, right, new ScaleSpaceFilter<>(2, 4, 8, 16, 32), "2_4_8_16_32");
//        handleFilter(left, right, new ScaleSpaceFilter<>(2, 4, 8, 16, 32, 64), "2_4_8_16_32_64");

        System.out.println(System.currentTimeMillis() - timestart);
    }
}
