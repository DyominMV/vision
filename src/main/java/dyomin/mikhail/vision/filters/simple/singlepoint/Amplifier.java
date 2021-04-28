package dyomin.mikhail.vision.filters.simple.singlepoint;

import dyomin.mikhail.vision.vectors.Vector;

public class Amplifier<V extends Vector<V>> implements SinglePointFilter<V,V> {
    public final double coefficient;

    public Amplifier(double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public V filter(V value) {
        return value.amplify(coefficient);
    }
}
