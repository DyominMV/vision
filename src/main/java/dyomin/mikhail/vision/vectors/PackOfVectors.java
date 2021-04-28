package dyomin.mikhail.vision.vectors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PackOfVectors<V extends Vector<V>> implements Vector<PackOfVectors<V>> {
    private final ArrayList<V> vectors;

    public PackOfVectors(ArrayList<V> vectors) {
        this.vectors = vectors;
    }

    public int size(){
        return this.vectors.size();
    }

    private PackOfVectors<V> useMapper(IntFunction<V> mapper) {
        return new PackOfVectors<>(
                IntStream.range(0, size())
                        .mapToObj(mapper)
                        .collect(Collectors.toCollection(() -> new ArrayList<>(size())))
        );
    }

    private PackOfVectors<V> useMapper(UnaryOperator<V> mapper) {
        return new PackOfVectors<>(
                vectors.stream()
                        .map(mapper)
                        .collect(Collectors.toCollection(() -> new ArrayList<>(size())))
        );
    }

    @Override
    public PackOfVectors<V> plus(PackOfVectors<V> other) {
        return useMapper((int i) -> this.vectors.get(i).plus(other.vectors.get(i)));
    }

    @Override
    public PackOfVectors<V> minus(PackOfVectors<V> other) {
        return useMapper((int i) -> this.vectors.get(i).minus(other.vectors.get(i)));
    }

    @Override
    public PackOfVectors<V> amplify(double coefficient) {
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

    public List<V> getVectors(){
        return vectors;
    }
}
