package dyomin.mikhail.vision.math.powerseries;

import dyomin.mikhail.vision.math.numeric.Numeric;
import dyomin.mikhail.vision.math.numeric.factory.NumericFactory;
import dyomin.mikhail.vision.math.powerseries.util.CompositionGenerator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class PowerSeriesBase<N extends Numeric<N>, PS extends PowerSeriesBase<N, PS>> implements PowerSeries<N, PS> {
    protected final List<N> coefficients;
    protected final NumericFactory<N> numerics;

    protected abstract PS buildFromCoefficients(List<N> coefficients);

    protected PowerSeriesBase(NumericFactory<N> numerics, List<N> coefficients) {
        this.coefficients = coefficients;
        this.numerics = numerics;
    }

    protected static <N extends Numeric<N>, PS extends PowerSeriesBase<N, PS>> PS withRoots(
            Function<List<N>, PS> builderFromCoefficients,
            NumericFactory<N> numerics,
            N[] roots
    ) {
        PS result = builderFromCoefficients.apply(
                Collections.singletonList(numerics.getOne())
        );

        for (N root : roots) {
            result = result.multiply(root).minus(result.moveRight());
        }

        return result;
    }

    @Override
    public N valueAt(N point) {
        N sum = numerics.getZero();

        ListIterator<N> coefficientIterator = coefficients.listIterator(coefficients.size());
        while (coefficientIterator.hasPrevious()) {
            sum = sum.multiply(point).plus(coefficientIterator.previous());
        }

        return sum;
    }

    @Override
    public PS moveRight() {
        return buildFromCoefficients(Stream
                .concat(
                        Stream.of(numerics.getZero()),
                        coefficients.stream()
                )
                .collect(Collectors.toList())
        );
    }

    @Override
    public PS moveLeft() {
        return buildFromCoefficients(
                coefficients.stream().skip(1).collect(Collectors.toList())
        );
    }

    @Override
    public PS compInverse(int resultMaxPower) {
        N denominator = coefficients.get(1);
        List<N> basicCoefficients = this.moveLeft().toMonic().moveRight().coefficients;

        List<N> resultCoefficients = new ArrayList<>();
        resultCoefficients.add(numerics.getZero());
        resultCoefficients.add(numerics.getOne());

        // b_n = -sum {for k = 1 .. n-1;  j1+j2+...+jk = n} b_k * a_j1 * a_j2 * ... * a_jk
        for (int n = 2; n < resultMaxPower + 1; n++) {
            N result_n = numerics.getZero();

            CompositionGenerator comps = new CompositionGenerator(n);

            for (int k = 1; k < n; k++) {
                result_n = result_n.minus(
                        resultCoefficients.get(k).multiply(comps
                                .getCompositions(k).stream()
                                .map(composition -> composition.stream()
                                        .map(index ->
                                                index < basicCoefficients.size()
                                                        ? basicCoefficients.get(index)
                                                        : numerics.getZero()
                                        )
                                        .reduce(N::multiply)
                                        .orElse(numerics.getZero())
                                )
                                .reduce(N::plus)
                                .orElse(numerics.getZero())
                        )
                );
            }

            resultCoefficients.add(result_n);
        }

        return buildFromCoefficients(resultCoefficients).multiply(denominator.invert());
    }

    @Override
    public PS negate() {
        return buildFromCoefficients(coefficients.stream()
                .map(N::negate)
                .collect(Collectors.toList())
        );
    }

    @Override
    public PS toMonic() {
        N firstCoefficient = coefficients.get(0);

        if (null == firstCoefficient) {
            return buildFromCoefficients(Collections.emptyList());
        }

        return buildFromCoefficients(coefficients.stream()
                .map(n -> n.divide(firstCoefficient))
                .collect(Collectors.toList())
        );
    }

    @Override
    public PS revertCoefficients() {
        ArrayList<N> clone = new ArrayList<>(coefficients);
        Collections.reverse(clone);
        return buildFromCoefficients(clone);
    }

    @Override
    public PS derivative() {
        return buildFromCoefficients(
                IntStream.range(1, coefficients.size())
                        .mapToObj(i -> coefficients.get(i).multiply(numerics.fromInteger(i)))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public PS integrate(N constant) {
        return buildFromCoefficients(Stream.concat(
                Stream.of(constant),
                IntStream.range(0, coefficients.size())
                        .mapToObj(i -> coefficients.get(i).divide(numerics.fromInteger(i + 1)))
        ).collect(Collectors.toList()));
    }

    @Override
    public PS trim(int newPower) {
        return buildFromCoefficients(coefficients.stream().limit(newPower+1).collect(Collectors.toList()));
    }

    @Override
    public PS multiply(N coefficient) {
        return buildFromCoefficients(coefficients.stream()
                .map(c -> c.multiply(coefficient))
                .collect(Collectors.toList())
        );
    }

    @Override
    public Stream<N> getCoefficients() {
        return coefficients.stream();
    }

    @Override
    public N nthCoefficient(int n) {
        return n < coefficients.size()
                ? coefficients.get(n)
                : numerics.getZero();
    }

    @Override
    public PS plus(PS other) {
        int i = 0;
        List<N> result = new ArrayList<>();

        for (; i < Math.min(this.coefficients.size(), other.coefficients.size()); i++) {
            result.add(this.coefficients.get(i).plus(other.coefficients.get(i)));
        }

        for (; i < this.coefficients.size(); i++) {
            result.add(this.coefficients.get(i));
        }

        for (; i < other.coefficients.size(); i++) {
            result.add(other.coefficients.get(i));
        }

        return buildFromCoefficients(result);
    }

    @Override
    public PS minus(PS other) {
        return plus(other.negate());
    }

    @Override
    public PS multiply(PS other) {
        if (0 == other.coefficients.size()) {
            return other;
        }

        if (0 == this.coefficients.size()) {
            return buildFromCoefficients(Collections.emptyList());
        }

        int resultPower = this.coefficients.size() + other.coefficients.size() - 1;
        List<N> result = new ArrayList<>(resultPower);

        for (int n = 0; n < resultPower; n++) {
            N sum = numerics.getZero();

            for (int i = 0; i <= n; i++) {
                sum = sum.plus(this.nthCoefficient(i).multiply(other.nthCoefficient(n - i)));
            }

            result.add(sum);
        }

        return buildFromCoefficients(result);
    }

    @Override
    public PS substitute(PS other) {
        PS sum = buildFromCoefficients(Collections.emptyList());

        ListIterator<N> coefficientIterator = coefficients.listIterator(coefficients.size());
        while (coefficientIterator.hasPrevious()) {
            sum = sum.multiply(other).plus(
                    buildFromCoefficients(Collections.singletonList(coefficientIterator.previous()))
            );
        }

        return sum;
    }

    @Override
    public String toString() {
        return IntStream.range(0, coefficients.size()).mapToObj(i ->
                coefficients.get(i).toString() + "x^" + i + "+"
        ).reduce((a, b) -> a + b).orElse("") + "0";
    }
}
