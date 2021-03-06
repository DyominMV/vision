package dyomin.mikhail.controllers;

import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.SimpleImageFilter;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.stereo.LeftDisparityMap;
import dyomin.mikhail.vision.stereo.PrimitiveStereo;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.io.File;
import java.io.IOException;

public class Stereo {
    public static void main(String[] args) throws IOException {
        LeftDisparityMap<RGB> disparityMap = new PrimitiveStereo<>(
                RgbImage.loadFromFile(new File(
                        Stereo.class.getResource("/photos/babyL.png").getFile()
                )),
                RgbImage.loadFromFile(new File(
                        Stereo.class.getResource("/photos/babyR.png").getFile()
                ))
        );

        RgbImage.fromSpecialImage(
                disparityMap.disparityMap
                        .applyFilter(
                                (SimpleImageFilter<Disparity, WrappedDouble>) (x, y, image) ->
                                        new WrappedDouble(image.getPixel(x, y).value - 100)
                        )
        ).writeToFile(new File(
                "/tmp/babyD.png"
        ));
//
//        System.out.println(disparityMap.getError(0, RgbImage.loadFromFile( new File(
//                Stereo.class.getResource("photos/babyD.png").getFile()
//        )).applyFilter(
//                (SimpleImageFilter<RGB, Disparity>) (x, y, image) -> new Disparity(image.getPixel(x,y).length())
//        )));
    }
}
