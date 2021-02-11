package dyomin.mikhail.vision.math.numeric;

public interface Numeric<N extends Numeric<N>> {
    N plus(N other);

    N multiply(N other);

    N negate();

    N invert();

    default N minus(N other) {
        return plus(other.negate());
    }

    default N divide(N other) {
        return multiply(other.invert());
    }
}
