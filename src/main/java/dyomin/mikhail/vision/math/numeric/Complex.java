package dyomin.mikhail.vision.math.numeric;

public class Complex implements Numeric<Complex> {
    public final double real;
    public final double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Complex(double real) {
        this(real, 0);
    }

    @Override
    public Complex plus(Complex other) {
        return new Complex(
                this.real + other.real,
                this.imaginary + other.imaginary
        );
    }

    @Override
    public Complex multiply(Complex other) {
        return new Complex(
                this.real * other.real - this.imaginary * other.imaginary,
                this.real * other.imaginary + this.imaginary * other.real
        );
    }

    @Override
    public Complex negate() {
        return new Complex(-real, -imaginary);
    }

    public Complex conjugate() {
        return new Complex(real, -imaginary);
    }

    public double modulus() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    public Complex amplify(double coefficient) {
        return new Complex(coefficient * real, coefficient * imaginary);
    }

    @Override
    public Complex invert() {
        return new Complex(real, -imaginary).amplify(1.0 / (real * real + imaginary * imaginary));
    }

    public double argument() {
        if (Double.MIN_NORMAL > real){
            return 0;
        }

        return Math.atan2(imaginary, real);
    }

    public static Complex ofModulusAndArgument(double modulus, double argument){
        return new Complex(modulus * Math.cos(argument), modulus*Math.sin(argument));
    }
}
