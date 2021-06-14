package dyomin.mikhail.vision.filters.simple.detector;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public class GutterDetector implements Detector<WrappedDouble> {

    @Override
    public boolean detect(int x, int y, ReadableImage<WrappedDouble> image) {
        double here = image.getPixel(x,y).value;
        double top = image.getPixel(x, y-1).value;
        double topLeft = image.getPixel(x-1, y-1).value;
        double left = image.getPixel(x-1, y).value;
        double bottomLeft = image.getPixel(x-1, y+1).value;
        double bottom = image.getPixel(x, y+1).value;
        double bottomRight = image.getPixel(x+1, y+1).value;
        double right = image.getPixel(x+1, y).value;
        double topRight = image.getPixel(x+1, y-1).value;

        double f_x = (right - left) / 2;
        double f_y = (bottom - top) / 2;

        double f_xx = right + left - 2 * here;
        double f_yy = top + bottom - 2 * here;

        double f_xy = topLeft + bottomRight - topRight - bottomLeft;

        double kAlong = f_x * f_x * f_xx + 2 * f_x * f_y * f_xy + f_y * f_y * f_yy;
        double kNormal =  f_y * f_y * f_xx - 2 * f_x * f_y * f_xy + f_x * f_x * f_yy;

        return kNormal >= 0 && Math.abs(kNormal) >= Math.abs(kAlong);
    }
}
