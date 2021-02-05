package dyomin.mikhail.vision.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerSeries<N extends Numeric<N>> {
    protected final List<N> coefficients;
    protected final N zero;

    protected PowerSeries(N zero, List<N> coefficients) {
        this.coefficients = coefficients;
        this.zero = zero;
    }

    protected static <N extends Numeric<N>> PowerSeries<N> withRoots(N zero, N initialCoefficient, N[] roots) {
        PowerSeries<N> result = new PowerSeries<>(zero, Collections.singletonList(initialCoefficient));

        for (N root : roots) {
            result = result.multiplyAll(root).minus(result.moveRight());
        }

        return result;
    }

    public N valueAt(N point) {
        N sum = zero;

        ListIterator<N> coefficientIterator = coefficients.listIterator(coefficients.size());
        while (coefficientIterator.hasPrevious()) {
            sum = sum.multiply(point).plus(coefficientIterator.previous());
        }

        return sum;
    }

    public PowerSeries<N> moveRight() {
        ArrayList<N> c = new ArrayList<>(coefficients);
        c.add(0, zero);
        return new PowerSeries<>(zero, c);
    }

    public PowerSeries<N> negate() {
        return new PowerSeries<>(zero, coefficients.stream().map(Numeric::negate).collect(Collectors.toList()));
    }

    public PowerSeries<N> plus(PowerSeries<N> other) {
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

        return new PowerSeries<>(zero, result);
    }

    public PowerSeries<N> minus(PowerSeries<N> other) {
        return this.plus(other.negate());
    }

    public PowerSeries<N> multiplyAll(N coefficient) {
        return new PowerSeries<>(zero, coefficients.stream().map(coefficient::multiply).collect(Collectors.toList()));
    }

    public PowerSeries<N> normalize(){
        return this.multiplyAll(
                coefficients.get(0).invert()
        );
    }

    public Stream<N> getCoefficients(){
        return coefficients.stream();
    }

    public PowerSeries<N> revertCoefficients(){
        List<N> clone = new ArrayList<>(coefficients);
        Collections.reverse(clone);
        return new PowerSeries<>(zero, clone);
    }
}
