package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public interface PathFinder {
    int[] findPath(ReadableImage<WrappedDouble> dsi);

    default RgbImage visualizePath(ReadableImage<WrappedDouble> dsi){
        RgbImage result = dsi.toRgbImage();

        int[] path = findPath(dsi);

        for (int i = 0; i < path.length; i++){
            result.setPixel(i, path[i], new RGB(1,-1,-1));
        }

        return result;
    }
}
