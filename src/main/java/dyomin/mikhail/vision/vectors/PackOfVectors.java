package dyomin.mikhail.vision.vectors;

import java.util.ArrayList;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * relies on an assumption that size() result is same for all instances of P
 */
public abstract class PackOfVectors<V extends Vector<V>, P extends PackOfVectors<V, P>> implements Vector<P> {
    protected final ArrayList<V> vectors;

    protected PackOfVectors(ArrayList<V> vectors) {
        this.vectors = vectors;
    }

    public abstract int size();

    protected abstract P generateFromVectors(ArrayList<V> vectors);

    private P useMapper(IntFunction<V> mapper) {
        return generateFromVectors(
                IntStream.range(0, size())
                        .mapToObj(mapper)
                        .collect(Collectors.toCollection(() -> new ArrayList<>(size())))
        );
    }

    private P useMapper(UnaryOperator<V> mapper) {
        return generateFromVectors(
                vectors.stream()
                        .map(mapper)
                        .collect(Collectors.toCollection(() -> new ArrayList<>(size())))
        );
    }

    @Override
    public P plus(P other) {
        return useMapper((int i) -> this.vectors.get(i).plus(other.vectors.get(i)));
    }

    @Override
    public P minus(P other) {
        return useMapper((int i) -> this.vectors.get(i).minus(other.vectors.get(i)));
    }

    @Override
    public P amplify(double coefficient) {
        return useMapper((V v) -> v.amplify(coefficient));
    }

    @Override
    public double length() {
        return Math.sqrt(
                vectors.stream()
                        .mapToDouble(V::length)
                        .map(x->x*x)
                        .sum()
        );
    }

    @Override
    public RGB visualize() {
        return vectors.stream()
                .map(V::visualize)
                .reduce(new RGB(0,0,0), RGB::plus)
                .amplify(1.0/size());
    }
}
