package dyomin.mikhail.vision.math.numeric.factory;

import dyomin.mikhail.vision.math.numeric.NumericDouble;

public class DoubleFactory implements NumericFactory<NumericDouble> {
    private DoubleFactory(){}

    public static final DoubleFactory FACTORY = new DoubleFactory();

    @Override
    public NumericDouble fromInteger(int n) {
        return new NumericDouble(n);
    }
}
