package dyomin.mikhail.vision.filters.simple.singlepoint;

import dyomin.mikhail.vision.vectors.Vector;

public class Suppressor<V extends Vector<V>> implements SinglePointFilter<V,V> {

    private final V suppressedValue;
    private final double threshold;

    public Suppressor(V suppressedValue, double threshold) {
        this.suppressedValue = suppressedValue;
        this.threshold = Math.abs(threshold);
    }

    @Override
    public V filter(V value) {
        double difference = suppressedValue.minus(value).length();

        return difference > threshold
                ? value
                : value.amplify(1 - difference / threshold);
    }
}
