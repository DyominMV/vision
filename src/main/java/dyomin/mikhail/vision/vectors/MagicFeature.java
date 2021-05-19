package dyomin.mikhail.vision.vectors;

import java.util.stream.DoubleStream;

public class MagicFeature<V extends Vector<V>> implements Vector<MagicFeature<V>> {
    private final V verticalGradient;
    private final V valueHere;

    public MagicFeature(V verticalGradient, V valueHere) {
        this.verticalGradient = verticalGradient;
        this.valueHere = valueHere;
    }

    @Override
    public MagicFeature<V> plus(MagicFeature<V> other) {
        return new MagicFeature<>(
                this.verticalGradient.plus(other.verticalGradient),
                this.valueHere.plus(other.valueHere)
        );
    }

    @Override
    public MagicFeature<V> minus(MagicFeature<V> other) {
        return this.plus(other.amplify(-1));
    }

    @Override
    public MagicFeature<V> amplify(double coefficient) {
        return new MagicFeature<>(verticalGradient.amplify(coefficient), valueHere.amplify(coefficient));
    }

    @Override
    public double length() {
        return Math.sqrt(
                DoubleStream.of(valueHere.length(), verticalGradient.length())
                        .map(x -> x * x)
                        .sum()
        );
    }

    @Override
    public RGB visualize() {
        return new WrappedDouble(length()).visualize();
    }
}
