package dyomin.mikhail.vision.math.numeric.factory;

import dyomin.mikhail.vision.math.numeric.Coefficient;
import dyomin.mikhail.vision.math.numeric.Numeric;

public interface CoefficientFactory<N extends Numeric<N>, C extends Coefficient<N, C>> {
    C fromInteger(int n);

    default C getZero(){
        return fromInteger(0);
    }

    default C getOne(){
        return fromInteger(1);
    }
}
