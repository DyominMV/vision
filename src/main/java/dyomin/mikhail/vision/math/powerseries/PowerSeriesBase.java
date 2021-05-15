package dyomin.mikhail.vision.math.powerseries;

import dyomin.mikhail.vision.math.numeric.Coefficient;
import dyomin.mikhail.vision.math.numeric.Numeric;
import dyomin.mikhail.vision.math.numeric.factory.CoefficientFactory;
import dyomin.mikhail.vision.math.numeric.factory.NumericFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class PowerSeriesBase<
        N extends Numeric<N>,
        C extends Coefficient<N, C>,
        PS extends PowerSeriesBase<N, C, PS>
        >
        implements
        PowerSeries<N, C, PS> {

    protected final List<C> coefficients;
    protected final CoefficientFactory<N, C> coefficientFactory;
    protected final NumericFactory<N> numericFactory;

    protected abstract PS buildFromCoefficients(List<C> coefficients);

    protected PowerSeriesBase(NumericFactory<N> numericFactory, CoefficientFactory<N, C> coefficientFactory, List<C> coefficients) {
        this.numericFactory = numericFactory;
        this.coefficients = coefficients;
        this.coefficientFactory = coefficientFactory;
    }

    public static <N extends Numeric<N>, C extends Coefficient<N, C>, PS extends PowerSeriesBase<N, C, PS>>
    PS withRoots(
            Function<List<C>, PS> builderFromCoefficients,
            CoefficientFactory<N, C> coefficientFactory,
            N[] roots
    ) {
        return withRoots(builderFromCoefficients, coefficientFactory, Arrays.asList(roots));
    }


    public static <N extends Numeric<N>, C extends Coefficient<N, C>, PS extends PowerSeriesBase<N, C, PS>>
    PS withRoots(
            Function<List<C>, PS> builderFromCoefficients,
            CoefficientFactory<N, C> coefficientFactory,
            List<N> roots
    ) {
        PS result = builderFromCoefficients.apply(
                Collections.singletonList(coefficientFactory.getOne())
        );

        for (N root : roots) {
            result = result.moveRight().minus(result.multiplyByNumeric(root));
        }

        return result;
    }

    public static <N extends Numeric<N>, C extends Coefficient<N, C>, PS extends PowerSeriesBase<N, C, PS>>
    PS ofPoints(
            Function<List<C>, PS> builderFromCoefficients,
            NumericFactory<N> numericFactory,
            CoefficientFactory<N, C> coefficientFactory,
            Map<N, C> points
    ) {
        List<Map.Entry<N, C>> pts = new ArrayList<>(points.entrySet());

        return IntStream.range(0, pts.size())
                .mapToObj(i -> {
                    List<N> xsNoI = pts.stream().map(Map.Entry::getKey).collect(Collectors.toList());

                    Map.Entry<N, C> ptI = pts.get(i);
                    N xI = ptI.getKey();
                    C yI = ptI.getValue();

                    xsNoI.remove(i);

                    N denominator = xsNoI.stream().map(xI::minus).reduce(N::plus).orElse(numericFactory.getOne());

                    return withRoots(builderFromCoefficients, coefficientFactory, xsNoI).divide(denominator).multiplyByCoefficient(yI);
                })
                .reduce(PS::plus)
                .orElse(builderFromCoefficients.apply(Collections.singletonList(coefficientFactory.getZero())));
    }

    public static <
            N extends Numeric<N>,
            C extends Coefficient<N, C>,
            PS extends PowerSeries<N, C, PS>
            >
    CoefficientFactory<N, PS> getFactory(
            Function<List<C>, PS> buildFromCoefficients,
            CoefficientFactory<N,C> coefficientFactory
    ) {
        return n -> buildFromCoefficients.apply(Collections.singletonList(coefficientFactory.fromInteger(n)));
    }

    @Override
    public C valueAt(N point) {
        C sum = coefficientFactory.getZero();

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
                        Stream.of(coefficientFactory.getZero()),
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
                        .mapToObj(i -> coefficients.get(i).multiplyByNumeric(numericFactory.fromInteger(i)))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public PS integrate(C constant) {
        return buildFromCoefficients(Stream.concat(
                Stream.of(constant),
                IntStream.range(0, coefficients.size())
                        .mapToObj(i -> coefficients.get(i).divide(numericFactory.fromInteger(i + 1)))
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
                : coefficientFactory.getZero();
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
            C sum = coefficientFactory.getZero();

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
