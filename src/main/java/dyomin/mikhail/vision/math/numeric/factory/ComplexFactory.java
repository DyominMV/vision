package dyomin.mikhail.vision.math.numeric.factory;

import dyomin.mikhail.vision.math.numeric.Complex;

public class ComplexFactory implements NumericFactory<Complex>{

    private ComplexFactory() {
    }

    public static final ComplexFactory FACTORY = new ComplexFactory();

    @Override
    public Complex fromInteger(int n) {
        return new Complex(n);
    }
}
