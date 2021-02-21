package dyomin.mikhail.vision.math.powerseries;

import dyomin.mikhail.vision.math.numeric.Coefficient;
import dyomin.mikhail.vision.math.numeric.Numeric;

import java.util.stream.Stream;

public interface PowerSeries<
        N extends Numeric<N>,
        C extends Coefficient<N, C>,
        PS extends PowerSeries<N, C, PS>
        >
        extends Coefficient<N, PS> {
    C valueAt(N point);

    PS moveRight();

    PS moveLeft();

    PS revertCoefficients();

    PS derivative();

    PS integrate(C constant);

    PS trim(int newPower);

    PS multiplyByCoefficient(C coefficient);

    Stream<C> getCoefficients();

    C nthCoefficient(int n);

    PS substitute(PS other);
}
