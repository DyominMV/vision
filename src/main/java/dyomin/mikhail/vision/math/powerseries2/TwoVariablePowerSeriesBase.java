package dyomin.mikhail.vision.math.powerseries2;

import dyomin.mikhail.vision.math.numeric.Numeric;
import dyomin.mikhail.vision.math.numeric.factory.NumericFactory;
import dyomin.mikhail.vision.math.powerseries.PowerSeries;
import dyomin.mikhail.vision.math.powerseries.PowerSeriesBase;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TwoVariablePowerSeriesBase<
        N extends Numeric<N>,
        PS extends PowerSeries<N, N, PS>,
        TVPS extends TwoVariablePowerSeriesBase<N, PS, TVPS>
        > extends PowerSeriesBase<N, PS, TVPS> implements TwoVariablesPowerSeries<N, PS, TVPS> {

    protected TwoVariablePowerSeriesBase(NumericFactory<N> numerics, List<PS> listOfSeries) {
        super(numerics, listOfSeries);
    }

    protected abstract PS buildRegularPowerSeries(List<N> numerics);

    @Override
    public N valueAt(N pointX, N pointY) {
        return valueAt(pointX).valueAt(pointY);
    }

    @Override
    public TVPS moveRightX() {
        return moveRight();
    }

    @Override
    public TVPS moveRightY() {
        return buildFromCoefficients(
                coefficients.stream().map(PS::moveRight).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS moveLeftX() {
        return moveLeft();
    }

    @Override
    public TVPS moveLeftY() {
        return buildFromCoefficients(
                coefficients.stream().map(PS::moveLeft).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS derivativeX() {
        return derivative();
    }

    @Override
    public TVPS derivativeY() {
        return buildFromCoefficients(
                coefficients.stream().map(PS::derivative).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS integrateX() {
        return integrate(getZeroCoefficient());
    }

    @Override
    public TVPS integrateY() {
        return buildFromCoefficients(
                coefficients.stream().map(ps -> ps.integrate(numerics.getZero())).collect(Collectors.toList())
        );
    }

    @Override
    public TVPS trim(int newPowerX, int newPowerY) {
        return buildFromCoefficients(
                trim(newPowerX).coefficients.stream()
                        .map(ps -> ps.trim(newPowerY))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Stream<Stream<N>> getNumericCoefficients() {
        return coefficients.stream().map(PS::getCoefficients);
    }

    @Override
    public N nthCoefficient(int powerOfX, int powerOfY) {
        return nthCoefficient(powerOfX).nthCoefficient(powerOfY);
    }

    @Override
    public TVPS substitute(TVPS otherX, TVPS otherY) {
        List<TVPS> substitutedYs = coefficients.stream()
                .map(otherY::substituteInto)
                .collect(Collectors.toList());

        TVPS sum = buildFromCoefficients(Collections.emptyList());

        ListIterator<TVPS> coefficientIterator = substitutedYs.listIterator(substitutedYs.size());
        while (coefficientIterator.hasPrevious()) {
            sum = sum.multiply(otherX).plus(
                    coefficientIterator.previous()
            );
        }

        return sum;
    }

    @Override
    public TVPS substituteInto(PS powerSeries) {
        TVPS sum = buildFromCoefficients(Collections.emptyList());
        TVPS copyOfThis = buildFromCoefficients(coefficients);

        List<N> seriesCoefficients = powerSeries.getCoefficients().collect(Collectors.toList());
        ListIterator<N> coefficientIterator = seriesCoefficients.listIterator(seriesCoefficients.size());
        while (coefficientIterator.hasPrevious()) {
            sum = sum.multiply(copyOfThis).plus(
                    buildFromCoefficients(
                            Collections.singletonList(
                                    buildRegularPowerSeries(
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
