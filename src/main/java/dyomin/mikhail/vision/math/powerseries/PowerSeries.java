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

    PS multiply(N coefficient);

    Stream<N> getCoefficients();

    PS plus(PS other);

    PS minus(PS other);

}
