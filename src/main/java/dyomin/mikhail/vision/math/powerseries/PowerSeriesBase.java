package dyomin.mikhail.vision.math.powerseries;

import dyomin.mikhail.vision.math.numeric.Coefficient;
import dyomin.mikhail.vision.math.numeric.Numeric;
import dyomin.mikhail.vision.math.numeric.factory.NumericFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class PowerSeriesBase<N extends Numeric<N>, C extends Coefficient<N, C>, PS extends PowerSeriesBase<N, C, PS>> implements PowerSeries<N, C, PS> {
    protected final List<C> coefficients;
    protected final NumericFactory<N> numerics;

    protected abstract PS buildFromCoefficients(List<C> coefficients);

    protected abstract C getZeroCoefficient();

    protected PowerSeriesBase(NumericFactory<N> numerics, List<C> coefficients) {
        this.coefficients = coefficients;
        this.numerics = numerics;
    }

    protected static <N extends Numeric<N>, C extends Coefficient<N, C>, PS extends PowerSeriesBase<N, C, PS>>
    PS withRoots(
            Function<List<N>, PS> builderFromCoefficients,
            NumericFactory<N> numerics,
            C[] roots
    ) {
        PS result = builderFromCoefficients.apply(
                Collections.singletonList(numerics.getOne())
        );

        for (C root : roots) {
            result = result.multiplyByCoefficient(root).minus(result.moveRight());
        }

        return result;
    }

    @Override
    public C valueAt(N point) {
        C sum = getZeroCoefficient();

        ListIterator<C> coefficientIterator = coefficients.listIterator(coefficients.size());
        while (coefficientIterator.hasPrevious()) {
            sum = sum.multiplyByNumeric(point).plus(coefficientIterator.previous());
        }

        return sum;
    }

    @Override
    public PS moveRight() {
        return buildFromCoefficients(Stream
                .concat(
                        Stream.of(getZeroCoefficient()),
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
    public PS negate() {
        return buildFromCoefficients(coefficients.stream()
                .map(C::negate)
                .collect(Collectors.toList())
        );
    }

    @Override
    public PS revertCoefficients() {
        ArrayList<C> clone = new ArrayList<>(coefficients);
        Collections.reverse(clone);
        return buildFromCoefficients(clone);
    }

    @Override
    public PS derivative() {
        return buildFromCoefficients(
                IntStream.range(1, coefficients.size())
                        .mapToObj(i -> coefficients.get(i).multiplyByNumeric(numerics.fromInteger(i)))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public PS integrate(C constant) {
        return buildFromCoefficients(Stream.concat(
                Stream.of(constant),
                IntStream.range(0, coefficients.size())
                        .mapToObj(i -> coefficients.get(i).divide(numerics.fromInteger(i + 1)))
        ).collect(Collectors.toList()));
    }

    @Override
    public PS trim(int newPower) {
        return buildFromCoefficients(coefficients.stream().limit(newPower + 1).collect(Collectors.toList()));
    }

    @Override
    public PS multiplyByCoefficient(C coefficient) {
        return buildFromCoefficients(coefficients.stream()
                .map(c -> c.multiply(coefficient))
                .collect(Collectors.toList())
        );
    }

    @Override
    public PS multiplyByNumeric(N numeric) {
        return buildFromCoefficients(coefficients.stream()
                .map((c -> c.multiplyByNumeric(numeric)))
                .collect(Collectors.toList())
        );
    }

    @Override
    public Stream<C> getCoefficients() {
        return coefficients.stream();
    }

    @Override
    public C nthCoefficient(int n) {
        return n < coefficients.size()
                ? coefficients.get(n)
                : getZeroCoefficient();
    }

    @Override
    public PS plus(PS other) {
        int i = 0;
        List<C> result = new ArrayList<>();

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
        List<C> result = new ArrayList<>(resultPower);

        for (int n = 0; n < resultPower; n++) {
            C sum = getZeroCoefficient();

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

        ListIterator<C> coefficientIterator = coefficients.listIterator(coefficients.size());
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
