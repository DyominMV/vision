package dyomin.mikhail.vision.math.numeric;

public interface Coefficient<N extends Numeric<N>, C extends Coefficient<N,C>>{
    C multiplyByNumeric(N numeric);

    default C divide(N numeric){
        return multiplyByNumeric(numeric.invert());
    }

    C negate();

    C plus(C other);

    default C minus(C other){
        return plus(other.negate());
    }

    C multiply(C other);
}
