package dyomin.mikhail.vision.math.numeric.factory;

import dyomin.mikhail.vision.math.numeric.Numeric;

public interface NumericFactory<N extends Numeric<N>> {
    N fromInteger(int n);

    default N getZero(){
        return fromInteger(0);
    }

    default N getOne(){
        return fromInteger(1);
    }
}
