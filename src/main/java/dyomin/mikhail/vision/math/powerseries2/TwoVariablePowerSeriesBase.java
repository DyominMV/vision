package dyomin.mikhail.vision.math.powerseries2;

import dyomin.mikhail.vision.math.numeric.Numeric;
import dyomin.mikhail.vision.math.numeric.factory.NumericFactory;
import dyomin.mikhail.vision.math.powerseries.PowerSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class TwoVariablePowerSeriesBase<
        N extends Numeric<N>,
        PS extends PowerSeries<N, PS>,
        TVPS extends TwoVariablePowerSeriesBase<N, PS, TVPS>
        > implements TwoVariablesPowerSeries<N, PS, TVPS> {

    protected final List<PS> listOfSeries;
    protected final NumericFactory<N> numerics;

    protected TwoVariablePowerSeriesBase(List<PS> listOfSeries, NumericFactory<N> numerics) {
        this.listOfSeries = listOfSeries;
        this.numerics = numerics;
    }

    protected abstract PS getZeroPowerSeries();

    protected abstract PS buildPowerSeries(List<N> coefficients);

    protected abstract TVPS buildTwoVariablePowerSeries(List<PS> listOfSeries);

    @Override
    public N valueAt(N pointX, N pointY) {
        return buildPowerSeries(
                listOfSeries.stream()
                        .map(s -> s.valueAt(pointY))
                        .collect(Collectors.toList())
        ).valueAt(pointX);
    }

    @Override
    public TVPS moveRightX() {
        return buildTwoVariablePowerSeries(
                Stream.concat(
                        Stream.of(getZeroPowerSeries()),
                        listOfSeries.stream()
                ).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS moveRightY() {
        return buildTwoVariablePowerSeries(
                listOfSeries.stream().map(PS::moveRight).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS moveLeftX() {
        return buildTwoVariablePowerSeries(
                listOfSeries.stream().skip(1).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS moveLeftY() {
        return buildTwoVariablePowerSeries(
                listOfSeries.stream().map(PS::moveLeft).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS negate() {
        return buildTwoVariablePowerSeries(
                listOfSeries.stream().map(PS::negate).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS toMonic() {
        N firstCoefficient = listOfSeries.stream()
                .findFirst()
                .orElse(getZeroPowerSeries())
                .getCoefficients()
                .findFirst()
                .orElse(numerics.getOne());
        N denominator = firstCoefficient.invert();

        return buildTwoVariablePowerSeries(
                listOfSeries.stream().map(ps -> ps.multiply(denominator)).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS derivativeX() {
        return buildTwoVariablePowerSeries(
                IntStream.range(1, listOfSeries.size())
                        .mapToObj(i -> listOfSeries.get(i).multiply(numerics.fromInteger(i)))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public TVPS derivativeY() {
        return buildTwoVariablePowerSeries(
                listOfSeries.stream().map(PS::derivative).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS integrateX() {
        return buildTwoVariablePowerSeries(
                IntStream.range(0, listOfSeries.size())
                        .mapToObj(
                                i -> listOfSeries.get(i)
                                        .multiply(numerics.fromInteger(i + 1).invert())
                        )
                        .collect(Collectors.toList())
        );
    }

    @Override
    public TVPS integrateY() {
        return buildTwoVariablePowerSeries(
                listOfSeries.stream().map(ps -> ps.integrate(numerics.getZero())).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS trim(int newPowerX, int newPowerY) {
        return buildTwoVariablePowerSeries(
                listOfSeries.stream()
                        .limit(newPowerX + 1)
                        .map(ps -> ps.trim(newPowerY))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public TVPS multiply(N coefficient) {
        return buildTwoVariablePowerSeries(
                listOfSeries.stream().map(ps -> ps.multiply(coefficient)).collect(Collectors.toList())
        );
    }

    @Override
    public Stream<Stream<N>> getCoefficients() {
        return listOfSeries.stream().map(PS::getCoefficients);
    }

    @Override
    public N nthCoefficient(int powerOfX, int powerOfY) {
        return powerOfX < listOfSeries.size()
                ? listOfSeries.get(powerOfX).nthCoefficient(powerOfY)
                : numerics.getZero();
    }

    protected PS nthSeries(int powerOfX) {
        return powerOfX < listOfSeries.size()
                ? listOfSeries.get(powerOfX)
                : getZeroPowerSeries();
    }

    @Override
    public TVPS plus(TVPS other) {
        int i = 0;
        List<PS> result = new ArrayList<>();

        for (; i < Math.min(this.listOfSeries.size(), other.listOfSeries.size()); i++) {
            result.add(this.listOfSeries.get(i).plus(other.listOfSeries.get(i)));
        }

        for (; i < this.listOfSeries.size(); i++) {
            result.add(this.listOfSeries.get(i));
        }

        for (; i < other.listOfSeries.size(); i++) {
            result.add(other.listOfSeries.get(i));
        }

        return buildTwoVariablePowerSeries(result);
    }

    @Override
    public TVPS minus(TVPS other) {
        return this.plus(other.negate());
    }

    @Override
    public TVPS multiply(TVPS other) {
        if (0 == other.listOfSeries.size()) {
            return other;
        }

        if (0 == this.listOfSeries.size()) {
            return buildTwoVariablePowerSeries(Collections.emptyList());
        }

        int resultPower = this.listOfSeries.size() + other.listOfSeries.size() - 1;
        List<PS> result = new ArrayList<>(resultPower);

        for (int n = 0; n < resultPower; n++) {
            PS sum = getZeroPowerSeries();

            for (int i = 0; i <= n; i++) {
                sum = sum.plus(this.nthSeries(i).multiply(other.nthSeries(n - i)));
            }

            result.add(sum);
        }

        return buildTwoVariablePowerSeries(result);
    }

    @Override
    public TVPS substitute(TVPS otherX, TVPS otherY) {
        List<TVPS> coefficients = listOfSeries.stream()
                .map(otherY::substituteInto)
                .collect(Collectors.toList());

        TVPS sum = buildTwoVariablePowerSeries(Collections.emptyList());

        ListIterator<TVPS> coefficientIterator = coefficients.listIterator(coefficients.size());
        while (coefficientIterator.hasPrevious()) {
            sum = sum.multiply(otherX).plus(coefficientIterator.previous());
        }

        return sum;
    }

    @Override
    public TVPS substituteInto(PS powerSeries) {
        TVPS sum = buildTwoVariablePowerSeries(Collections.emptyList());
        TVPS copyOfThis = buildTwoVariablePowerSeries(listOfSeries);

        List<N> coefficients = powerSeries.getCoefficients().collect(Collectors.toList());

        ListIterator<N> coefficientIterator = coefficients.listIterator(coefficients.size());
        while (coefficientIterator.hasPrevious()) {
            sum = sum.multiply(copyOfThis).plus(
                    buildTwoVariablePowerSeries(
                            Collections.singletonList(
                                    buildPowerSeries(
                                            Collections.singletonList(
                                                    coefficientIterator.previous()
                                            )
                                    )
                            )
                    )
            );
        }

        return sum;
    }
}
