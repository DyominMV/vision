package dyomin.mikhail.vision.math.powerseries2;

import dyomin.mikhail.vision.math.numeric.Numeric;
import dyomin.mikhail.vision.math.powerseries.PowerSeries;

import java.util.stream.Stream;

public interface TwoVariablesPowerSeries<N extends Numeric<N>, PS extends PowerSeries<N,PS>, TVPS extends TwoVariablesPowerSeries<N, PS, TVPS>> {
    N valueAt(N pointX, N pointY);

    TVPS moveRightX();

    TVPS moveRightY();

    TVPS moveLeftX();

    TVPS moveLeftY();

    TVPS negate();

    TVPS toMonic();

    TVPS derivativeX();

    TVPS derivativeY();

    TVPS integrateX();

    TVPS integrateY();

    TVPS trim(int newPowerX, int newPowerY);

    TVPS multiply(N coefficient);

    Stream<Stream<N>> getCoefficients();

    N nthCoefficient(int powerOfX, int powerOfY);

    TVPS plus(TVPS other);

    TVPS minus(TVPS other);

    TVPS multiply(TVPS other);

    TVPS substitute(TVPS otherX, TVPS otherY);

    TVPS substituteInto(PS powerSeries);
}
