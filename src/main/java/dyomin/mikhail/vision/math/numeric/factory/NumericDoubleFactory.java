package dyomin.mikhail.vision.math.numeric.factory;

import dyomin.mikhail.vision.math.numeric.NumericDouble;

public class NumericDoubleFactory implements NumericFactory<NumericDouble>{
    private NumericDoubleFactory(){}

    @Override
    public NumericDouble fromInteger(int n) {
        return new NumericDouble(n);
    }

    public static final NumericDoubleFactory FACTORY = new NumericDoubleFactory();
}
