package dyomin.mikhail.vision.vectors;

/**
 * Это очередной класс для представления цвета.
 * Здесь каждая составляющая имеет значение от -0.5 до 0.5.
 * <p>
 * -1 - это минимальная допустимая яркость (чёрный)
 * 0 - это средняя яркость (серый)
 * 1 - это максимальная яркость (белый)
 * <p>
 * Возможны ситуации, когда значение выходит за эти рамки. Такая возможность оставлена для корректности вычислений.
 */
public class RGB implements Vector<RGB> {
    public final double red;
    public final double green;
    public final double blue;

    private double fromInt(int part) {
        return ((part) - 127.5) / 127.5;
    }

    private int toInt(double part) {
        int res = (int) Math.round(part * 127.5 + 127.5);
        return Math.min(Math.max(res, 0), 255);
    }

    public RGB(int encoded) {
        this.blue = fromInt(encoded & 0xFF);
        this.green = fromInt((encoded >> 8) & 0xFF);
        this.red = fromInt((encoded >> 16) & 0xFF);
    }

    public int encode() {
        return toInt(blue) + (toInt(green) << 8) + (toInt(red) << 16) + (0xFF << 24);
    }

    public RGB(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public RGB plus(RGB other) {
        return new RGB(
                this.red + other.red,
                this.green + other.green,
                this.blue + other.blue
        );
    }

    @Override
    public RGB minus(RGB other) {
        return new RGB(
                this.red - other.red,
                this.green - other.green,
                this.blue - other.blue
        );
    }

    @Override
    public double product(RGB other) {
        return this.red * other.red +
                this.green * other.green +
                this.blue * other.blue;
    }

    @Override
    public RGB amplify(double coefficient) {
        return new RGB(
                coefficient * red,
                coefficient * green,
                coefficient * blue
        );
    }

    @Override
    public double length() {
        return Math.sqrt(this.product(this));
    }

    public static final double MAX_LENGTH = Math.sqrt(3);

    @Override
    public RGB visualize() {
        return this;
    }

    public static RGB gray(double val) {
        return new RGB(val, val, val);
    }

    public RGB grayScale() {
        return gray((red + green + blue) / 3);
    }

    @Override
    public String toString() {
        return "RGB{" +
                "r=" + red +
                ", g=" + green +
                ", b=" + blue +
                '}';
    }

    public RGB activateToColor() {
        return activateToBoolean() ?
                new RGB(1, 1, 1) :
                new RGB(-1, -1, -1);
    }

    public boolean activateToBoolean() {
        return ((red > 0 ? 1 : -1) +
                (green > 0 ? 1 : -1) +
                (blue > 0 ? 1 : -1)
        ) > 0;
    }
}
