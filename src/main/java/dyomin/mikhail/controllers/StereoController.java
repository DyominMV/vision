package dyomin.mikhail.controllers;

import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.Gradient;
import dyomin.mikhail.vision.filters.simple.ScaleSpaceFilter;
import dyomin.mikhail.vision.filters.simple.detector.BelowLengthThreshold;
import dyomin.mikhail.vision.filters.simple.detector.GutterDetector;
import dyomin.mikhail.vision.filters.simple.singlepoint.Amplifier;
import dyomin.mikhail.vision.filters.simple.singlepoint.SinglePointFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.stereo.DsiStereo;
import dyomin.mikhail.vision.stereo.Stereo;
import dyomin.mikhail.vision.stereo.dsi.DsiFactory;
import dyomin.mikhail.vision.stereo.dsipathfinders.*;
import dyomin.mikhail.vision.vectors.PackOfVectors;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.WrappedBoolean;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.io.File;
import java.io.IOException;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class StereoController {

    public static void main(String[] args) throws IOException {
        long timestart = System.currentTimeMillis();

        ReadableImage<RGB> left = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyL.png").getFile()
        ));

        ReadableImage<RGB> right = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyR.png").getFile()
        ));

        final int ROW_NUMBER = 70;

        RgbImage leftCopy = left.applyFilter((SinglePointFilter<RGB, RGB>) value -> value).toRgbImage();
        for (int i = 0; i < leftCopy.getWidth(); i++) {
            leftCopy.setPixel(i, ROW_NUMBER, new RGB(1, -1, -1));
        }
        leftCopy.writeToFile(new File("/tmp/selected_line.png"));


        ReadableImage<WrappedDouble> unfilteredDsi = new DsiFactory<PackOfVectors<RGB>>((p1, p2) -> p1.minus(p2).length()).getDsi(
                left.applyFilter(new ScaleSpaceFilter<>(2, 4, 8, 16)).getRow(ROW_NUMBER).collect(Collectors.toList()),
                right.applyFilter(new ScaleSpaceFilter<>(2, 4, 8, 16)).getRow(ROW_NUMBER).collect(Collectors.toList())
        )
                .applyFilter(new PseudoGaussianBlur<>(5, SystemOrder.FIVE));

        ReadableImage<WrappedBoolean> filteredDsi = unfilteredDsi
                .applyFilter(
                        new Gradient()
                                .detecting(grad ->
                                        Math.abs(grad.normalize().rotate(-Math.PI / 4).x) <= 0.4
                                )
                                .and(new GutterDetector())
                                .and(new BelowLengthThreshold<>(0.5))
                );

        new BoolSimpleGraphPathFinder().visualizePath(filteredDsi).writeToFile(new File("/tmp/vpath_f.png"));
        new SimpleGraphPathFinder().visualizePath(unfilteredDsi).writeToFile(new File("/tmp/vpath_uf.png"));
        filteredDsi.toRgbImage().writeToFile(new File("/tmp/f.png"));
        unfilteredDsi.toRgbImage().writeToFile(new File("/tmp/uf.png"));


        Stereo<RGB> stereoBase =
                new DsiStereo<PackOfVectors<RGB>>(
//                        new SimpleGraphPathFinder()
                        //new FilteredPathFinder(
                                new DetectedPathFinder(
                                        new BoolSimpleGraphPathFinder(),
                                        new Gradient()
                                                .detecting(grad ->
                                                        Math.abs(grad.normalize().rotate(-Math.PI / 4).x) <= 0.4
                                                )
                                                .and(new GutterDetector())
                                                .and(new BelowLengthThreshold<>(0.5))
                                )
                        //,
                                //new PseudoGaussianBlur<>(5, SystemOrder.FIVE)
                        //)
                        ,
                        (p1, p2) -> p1.minus(p2).length()
                )
                        .filtered(new ScaleSpaceFilter<>(2, 4, 8, 16));

        stereoBase.getDisparityMap(left.scale(0.5), right.scale(0.5))
                .applyFilter(new Amplifier<>(2))
                .toRgbImage()
                .writeToFile(new File("/tmp/babyDmap_filtered.png"));

        System.out.println(System.currentTimeMillis() - timestart);
    }
}
