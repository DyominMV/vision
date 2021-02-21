package dyomin.mikhail.vision.math.numeric;

public interface Numeric<N extends Numeric<N>> extends Coefficient<N, N> {
    N invert();
}
