package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.vectors.WrappedBoolean;

public interface BoolDsiPathFinder {
    int[] findPath(ReadableImage<WrappedBoolean> dsi);

    default RgbImage visualizePath(ReadableImage<WrappedBoolean> dsi){
        RgbImage result = dsi.toRgbImage();

        int[] path = findPath(dsi);

        for (int i = 0; i < path.length; i++){
            result.setPixel(i, path[i], new RGB(1,-1,-1));
        }

        return result;
    }
}
