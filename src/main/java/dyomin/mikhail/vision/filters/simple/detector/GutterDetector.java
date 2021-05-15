package dyomin.mikhail.vision.filters.simple.detector;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.WrappedDouble;

public class GutterDetector implements Detector<WrappedDouble> {

    @Override
    public boolean detect(int x, int y, ReadableImage<WrappedDouble> image) {
        double here = image.getSubpixel(x,y).value;
        double top = image.getSubpixel(x, y-3).value;
        double topLeft = image.getSubpixel(x-3, y-3).value;
        double left = image.getSubpixel(x-3, y).value;
        double bottomLeft = image.getSubpixel(x-3, y+3).value;
        double bottom = image.getSubpixel(x, y+3).value;
        double bottomRight = image.getSubpixel(x+3, y+3).value;
        double right = image.getSubpixel(x+3, y).value;
        double topRight = image.getSubpixel(x+3, y-3).value;

        double f_x = (right - left) /2;
        double f_y = (bottom - top)/2;

        double f_xx = right + left - 2 * here;
        double f_yy = top + bottom - 2 * here;

        double f_xy = topLeft + bottomRight - topRight - bottomLeft;

        double kAlong = f_x * f_x * f_xx + 2 * f_x * f_y * f_xy + f_y * f_y * f_yy;
        double kNormal =  f_y * f_y * f_xx - 2 * f_x * f_y * f_xy + f_x * f_x * f_yy;

        return kNormal >= 0 && Math.abs(kNormal) >= Math.abs(kAlong);
    }
}
