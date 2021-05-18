package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.math.numeric.Coefficient;
import dyomin.mikhail.vision.math.numeric.NumericDouble;
import dyomin.mikhail.vision.math.numeric.factory.CoefficientFactory;
import dyomin.mikhail.vision.math.numeric.factory.DoubleFactory;
import dyomin.mikhail.vision.math.powerseries.PowerSeriesBase;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PowerSeriesStereo<V extends Vector<V>> implements LineByLineStereo<V> {

    private final BinaryOperator<V> adamarProd;
    private final IntFunction<V> simpleVectorProducer;

    public PowerSeriesStereo(BinaryOperator<V> adamarProd, IntFunction<V> simpleVectorProducer) {
        this.adamarProd = adamarProd;
        this.simpleVectorProducer = simpleVectorProducer;
    }

    private class VectorCoefficient implements Coefficient<NumericDouble, VectorCoefficient> {
        private final V value;

        public VectorCoefficient(V value) {
            this.value = value;
        }

        @Override
        public VectorCoefficient multiplyByNumeric(NumericDouble numeric) {
            return new VectorCoefficient(value.amplify(numeric.value));
        }

        @Override
        public VectorCoefficient negate() {
            return new VectorCoefficient(value.amplify(-1));
        }

        @Override
        public VectorCoefficient plus(VectorCoefficient other) {
            return new VectorCoefficient(value.plus(other.value));
        }

        @Override
        public VectorCoefficient multiply(VectorCoefficient other) {
            return new VectorCoefficient(adamarProd.apply(value, other.value));
        }
    }

    private class VectorCoefficientFactory implements CoefficientFactory<NumericDouble, VectorCoefficient> {

        @Override
        public VectorCoefficient fromInteger(int n) {
            return new VectorCoefficient(simpleVectorProducer.apply(n));
        }
    }

    private class VectorPowerSeries extends PowerSeriesBase<NumericDouble, VectorCoefficient, VectorPowerSeries> {

        public VectorPowerSeries(List<VectorCoefficient> coefficients) {
            super(DoubleFactory.FACTORY, new VectorCoefficientFactory(), coefficients);
        }

        @Override
        protected VectorPowerSeries buildFromCoefficients(List<VectorCoefficient> coefficients) {
            return new VectorPowerSeries(coefficients);
        }
    }

    private VectorPowerSeries toPowerSeries(List<V> row) {
        return PowerSeriesBase.ofPoints(
                VectorPowerSeries::new,
                DoubleFactory.FACTORY,
                new VectorCoefficientFactory(),
                IntStream.range(0, row.size())
                        .boxed()
                        .collect(Collectors.toMap(
                                i -> new NumericDouble(i.doubleValue()),
                                i -> new VectorCoefficient(row.get(i))
                        ))
        );
    }

    @Override
    public List<Disparity> getDisparities(List<V> left, List<V> right) {

        VectorPowerSeries lps = toPowerSeries(left);
        VectorPowerSeries rps = toPowerSeries(right);

        

        return left.stream().map(v->new Disparity(v.length())).collect(Collectors.toList());
    }
}
