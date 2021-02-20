package dyomin.mikhail.vision.math.powerseries;

import dyomin.mikhail.vision.math.numeric.Numeric;

import java.util.stream.Stream;

public interface PowerSeries<N extends Numeric<N>, PS extends PowerSeries<N, PS>> {
    N valueAt(N point);

    PS moveRight();

    PS moveLeft();

    PS compInverse(int resultMaxPower);

    PS negate();

    PS toMonic();

    PS revertCoefficients();

    PS derivative();

    PS integrate(N constant);

    PS trim(int newPower);

    PS multiply(N coefficient);

    Stream<N> getCoefficients();

    N nthCoefficient(int n);

    PS plus(PS other);

    PS minus(PS other);

    PS multiply(PS other);

    PS substitute(PS other);
}
