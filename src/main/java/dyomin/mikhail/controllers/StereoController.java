package dyomin.mikhail.controllers;

import dyomin.mikhail.vision.filters.simple.ScaleSpaceFilter;
import dyomin.mikhail.vision.filters.simple.SmallMedian;
import dyomin.mikhail.vision.filters.simple.singlepoint.Amplifier;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.stereo.DsiStereo;
import dyomin.mikhail.vision.stereo.FilteredStereo;
import dyomin.mikhail.vision.stereo.Stereo;
import dyomin.mikhail.vision.stereo.dsipathfinders.*;
import dyomin.mikhail.vision.vectors.*;

import java.io.File;
import java.io.IOException;

public class StereoController {

    public static void main(String[] args) throws IOException {
        long timestart = System.currentTimeMillis();

        ReadableImage<RGB> left = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyL.png").getFile()
        )).scale(0.5);

        ReadableImage<RGB> right = RgbImage.loadFromFile(new File(
                StereoController.class.getResource("/photos/babyR.png").getFile()
        )).scale(0.5);

//        final int ROW_NUMBER = 250;
//
//        RgbImage leftCopy = left.applyFilter((SinglePointFilter<RGB, RGB>) value -> value).toRgbImage();
//        for (int i = 0; i < leftCopy.getWidth(); i++) {
//            leftCopy.setPixel(i, ROW_NUMBER, new RGB(1, -1, -1));
//        }
//        leftCopy.writeToFile(new File("/tmp/selected_line.png"));
//
//
//        ReadableImage<WrappedDouble> unfilteredDsi = new DsiFactory<PackOfVectors<RGB>>(
//                (p1, p2)-> p1.minus(p2).length()
//        )
//                .getDsi(
//                        left
//                                .applyFilter(new ScaleSpaceFilter<>(2, 4, 8, 16, 32))
//                                .getRow(ROW_NUMBER)
//                                .collect(Collectors.toList()),
//                        right
//                                .applyFilter(new ScaleSpaceFilter<>(2, 4, 8, 16, 32))
//                                .getRow(ROW_NUMBER)
//                                .collect(Collectors.toList())
//                )
//                .applyFilter(new PseudoGaussianBlur<>(3, SystemOrder.FIVE));
//
//        ReadableImage<WrappedDouble> filteredDsi = unfilteredDsi
//                .applyFilter(
//                        //new GutterDetector()
//                        new Replacer<>(
//                                new Gradient().detecting(
//                                        grad -> Math.abs(grad.normalize().rotate(Math.PI / 4).x) <= 0.5
//                                )
//                                        .and(new GutterDetector())
//                                        .and(new BelowLengthThreshold<>(0.4))
//                                        .not(),
//                                new WrappedDouble(1000)
//                        )
//                );
//
//        new JumpingGraphPathFinder(0.5).visualizePath(filteredDsi).writeToFile(new File("/tmp/vpath_f.png"));
//        new JumpingGraphPathFinder(0.5).visualizePath(unfilteredDsi).writeToFile(new File("/tmp/vpath_uf.png"));
//        filteredDsi.toRgbImage().writeToFile(new File("/tmp/f.png"));
//        unfilteredDsi.toRgbImage().writeToFile(new File("/tmp/uf.png"));

        for (double jumpCost : new double[]{0.005, 0.01, 0.02, 0.03, 0.04, 0.05}){
            Stereo<RGB> stereoBase = new FilteredStereo<RGB, PackOfVectors<RGB>>(
                    new DsiStereo<>(
                            new VerticallyJumpingGraphPathFinder(jumpCost),
                            (p1, p2) -> p1.minus(p2).length()
                    ),
                    new ScaleSpaceFilter<>(2, 4, 8, 16, 32)
            );

            ReadableImage<Disparity> disparityMap = stereoBase.getDisparityMap(left, right)
                    .applyFilter(new Amplifier<>(2));

            disparityMap.applyFilter(new SmallMedian<>())
                    .toRgbImage()
                    .writeToFile(new File("/tmp/"+jumpCost+"_babyDmap_filtered.png"));

            disparityMap.toRgbImage()
                    .writeToFile(new File("/tmp/"+jumpCost+"_babyDmap.png"));
        }

        System.out.println(System.currentTimeMillis() - timestart);
    }
}
