package dyomin.mikhail.vision.math.powerseries2;

import dyomin.mikhail.vision.math.numeric.Numeric;
import dyomin.mikhail.vision.math.powerseries.PowerSeries;

import java.util.stream.Stream;

public interface TwoVariablesPowerSeries<
        N extends Numeric<N>,
        PS extends PowerSeries<N, N, PS>,
        TVPS extends TwoVariablesPowerSeries<N, PS, TVPS>
        >
        extends PowerSeries<N, PS, TVPS> {
    N valueAt(N pointX, N pointY);

    TVPS moveRightX();

    TVPS moveRightY();

    TVPS moveLeftX();

    TVPS moveLeftY();

    TVPS derivativeX();

    TVPS derivativeY();

    TVPS integrateX();

    TVPS integrateY();

    TVPS trim(int newPowerX, int newPowerY);

    Stream<Stream<N>> getNumericCoefficients();

    N nthCoefficient(int powerOfX, int powerOfY);

    TVPS substitute(TVPS otherX, TVPS otherY);

    TVPS substituteInto(PS powerSeries);
}
