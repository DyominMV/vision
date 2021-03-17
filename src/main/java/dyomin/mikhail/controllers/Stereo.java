package dyomin.mikhail.controllers;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.stereo.StereoBase;
import dyomin.mikhail.vision.stereo.PrimitiveStereo;
import dyomin.mikhail.vision.vectors.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stereo {
    static class ScaleSpaceRGB extends PackOfVectors<RGB, ScaleSpaceRGB> {

        protected ScaleSpaceRGB(RGB scale1, RGB scale2, RGB scale3, RGB scale4) {
            this(Stream.of(scale1, scale2, scale3, scale4).collect(Collectors.toCollection(
                    () -> new ArrayList<>(4)
            )));
        }

        private ScaleSpaceRGB(ArrayList<RGB> vectors) {
            super(vectors);
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        protected ScaleSpaceRGB generateFromVectors(ArrayList<RGB> vectors) {
            return new ScaleSpaceRGB(vectors);
        }
    }

    static class ScaleSpaceFilter implements ImageFilter<RGB, ScaleSpaceRGB> {

        private final PseudoGaussianBlur<RGB> pgb1 = new PseudoGaussianBlur<>(40, SystemOrder.FIVE);
        private final PseudoGaussianBlur<RGB> pgb2 = new PseudoGaussianBlur<>(10, SystemOrder.FIVE);
        private final PseudoGaussianBlur<RGB> pgb3 = new PseudoGaussianBlur<>(20, SystemOrder.FIVE);

        @Override
        public void filter(ReadableImage<RGB> image, EditableImage<ScaleSpaceRGB> output) {
            ReadableImage<RGB> image1 = image.applyFilter(pgb1);
            ReadableImage<RGB> image2 = image.applyFilter(pgb2);
            ReadableImage<RGB> image3 = image.applyFilter(pgb3);

            for (int x = 0; x < output.getWidth(); x++) {
                for (int y = 0; y < output.getHeight(); y++) {
                    output.setPixel(x, y, new ScaleSpaceRGB(
                            image.getPixel(x, y),
                            image1.getPixel(x, y),
                            image2.getPixel(x, y),
                            image3.getPixel(x, y)
                    ));
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        ScaleSpaceFilter fltr = new ScaleSpaceFilter();

        long timestart = System.currentTimeMillis();

        EditableImage<RGB> left = RgbImage.loadFromFile(new File(
                Stereo.class.getResource("/photos/babyL.png").getFile()
        )).toMatrixImage();//.applyFilter(fltr);

        EditableImage<RGB> right = RgbImage.loadFromFile(new File(
                Stereo.class.getResource("/photos/babyR.png").getFile()
        )).toMatrixImage();//.applyFilter(fltr);

        StereoBase<RGB> stereoBase = new PrimitiveStereo<>(
                left,
                right,
                0.05
        );

        RgbImage.fromSpecialImage(
                stereoBase.getDisparityMap()
                        .applyFilter(
                                (SimpleImageFilter<Disparity, WrappedDouble>) (x, y, image) ->
                                        new WrappedDouble(image.getPixel(x, y).value - 100)
                        )
        ).writeToFile(new File(
                "/tmp/babyD.png"
        ));

        System.out.println(System.currentTimeMillis() - timestart);
    }
}
