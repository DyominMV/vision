package dyomin.mikhail.controllers;

import dyomin.mikhail.vision.filters.Normalizer;
import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.*;
import dyomin.mikhail.vision.filters.simple.detector.GutterDetector;
import dyomin.mikhail.vision.filters.simple.singlepoint.Amplifier;
import dyomin.mikhail.vision.filters.simple.singlepoint.SinglePointFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.stereo.DsiStereo;
import dyomin.mikhail.vision.stereo.FilteredStereo;
import dyomin.mikhail.vision.stereo.Stereo;
import dyomin.mikhail.vision.stereo.dsi.DsiFactory;
import dyomin.mikhail.vision.stereo.dsipathfinders.*;
import dyomin.mikhail.vision.vectors.*;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class StereoController {

    public static void main(String[] args) throws IOException {
        long timestart = System.currentTimeMillis();

        ReadableImage<RGB> left = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyL.png").getFile()
        )).scale(0.5);

        ReadableImage<RGB> right = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyR.png").getFile()
        )).scale(0.5);

//        PathFinder pathFinder = new PiecewiseLinearPathFinder((j)->j*0.08 + 0.08* Math.sqrt(j) / 150);
        PathFinder pathFinder = new PiecewiseConstantPathFinder((e, j)->e + j*0.08 + 0.08* Math.sqrt(j) / 150);

        final int ROW_NUMBER = 220;

        RgbImage leftCopy = left.toRgbImage();
        for (int i = 0; i < leftCopy.getWidth(); i++) {
            leftCopy.setPixel(i, ROW_NUMBER, new RGB(1, -1, -1));
        }
        leftCopy.writeToFile(new File("/tmp/selected_line_L.png"));

        ReadableImage<WrappedDouble> unfilteredDsi = new DsiFactory<RGB>(
                (p1, p2) -> p1.minus(p2).length()
        )
                .getDsi(
                        left.getRow(ROW_NUMBER).collect(Collectors.toList()),
                        right.getRow(ROW_NUMBER).collect(Collectors.toList())
                )
//                .applyFilter(new PseudoGaussianBlur<>(3, SystemOrder.FIVE))
//                .applyFilter(
//                        new Replacer<>(
//                                new Gradient().detecting(dir ->
//                                        Math.abs(dir.normalize().rotate(Math.PI / 4).x) <= 0.5
//                                )
////                                        .and(new GutterDetector())
//                                        .not()
//                                ,
//                                new WrappedDouble(1)
//                        )
//                )
                ;

        pathFinder.visualizePath(unfilteredDsi).writeToFile(new File("/tmp/path.png"));

        unfilteredDsi.toRgbImage().writeToFile(new File("/tmp/uf.png"));

        Stereo<RGB> stereoBase =
                new DsiStereo<>(
                        new FilteredPathFinder(
                                pathFinder,
                                new Normalizer<>()
                        ),
                        (p1, p2) -> p1.minus(p2).length()
        );

        ReadableImage<Disparity> disparityMap = stereoBase.getDisparityMap(left, right)
                .applyFilter(new Amplifier<>(2));

        disparityMap.applyFilter(new SmallMedian<>())
                .toRgbImage()
                .writeToFile(new File("/tmp/" + "dmap" + "_babyDmap_filtered.png"));

        disparityMap.toRgbImage()
                .writeToFile(new File("/tmp/" + "dmap" + "_babyDmap.png"));


        System.out.println(System.currentTimeMillis() - timestart);
    }
}
