package dyomin.mikhail.vision.filters.gauss;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.images.EditableImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.math.numeric.Complex;
import dyomin.mikhail.vision.math.numeric.factory.ComplexFactory;
import dyomin.mikhail.vision.math.powerseries.ComplexPowerSeries;
import dyomin.mikhail.vision.math.powerseries.PowerSeriesBase;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class PseudoGaussianBlur<V extends Vector<V>> implements ImageFilter<V, V> {

    private static final Complex[] THIRD_ORDERED_Ds = new Complex[]{
            new Complex(1.4165, 1.00829),
            new Complex(1.4165, 1.00829).conjugate(),
            new Complex(1.86543)
    };
    private static final Complex[] FOURTH_ORDERED_Ds = new Complex[]{
            new Complex(1.13228, 1.28114),
            new Complex(1.13228, 1.28114).conjugate(),
            new Complex(1.78534, 0.46763),
            new Complex(1.78534, 0.46763).conjugate()
    };
    private static final Complex[] FIFTH_ORDERED_Ds = new Complex[]{
            new Complex(0.86430, 1.45389),
            new Complex(0.86430, 1.45389).conjugate(),
            new Complex(1.61433, 0.83134),
            new Complex(1.61433, 0.83134).conjugate(),
            new Complex(1.87504)
    };

    private static final double SIGMAS = 8;

    public final double sigma;
    public final SystemOrder systemOrder;

    private final double[] bCoefficients;
    private final double aCoefficient;
    private final double[] fairWeights;
    private final int lag;

    public PseudoGaussianBlur(double sigma, SystemOrder systemOrder) {
        this.sigma = sigma;
        this.systemOrder = systemOrder;

        final Complex[] ds;
        double q = sigma / 2;

        switch (systemOrder) {
            case THREE:
                ds = THIRD_ORDERED_Ds;
                break;
            case FOUR:
                ds = FOURTH_ORDERED_Ds;
                break;
            case FIVE:
            default:
                ds = FIFTH_ORDERED_Ds;
        }

        ComplexPowerSeries b = ComplexPowerSeries.withRoots(
                Arrays.stream(ds)
                        .map(di -> Complex.ofModulusAndArgument(
                                Math.pow(di.modulus(), 1.0 / q),
                                di.argument() / q
                        ))
                        .map(Complex::invert)
                        .toArray(Complex[]::new)
        ).revertCoefficients();

        if (systemOrder.steps % 2 != 0){
            b = b.negate();
        }

        bCoefficients = b.getCoefficients().mapToDouble(c -> c.real).toArray();
        bCoefficients[0] = 0;

        aCoefficient = b.valueAt(ComplexFactory.FACTORY.getOne()).real;

        fairWeights = new double[(int) (sigma * SIGMAS) + systemOrder.steps];
        fairWeights[systemOrder.steps] = 1;

        for (int x = systemOrder.steps; x < fairWeights.length; x++) {
            double sum = aCoefficient * fairWeights[x];

            for (int i = 1; i < systemOrder.steps + 1; i++) {
                sum -= bCoefficients[i] * fairWeights[x - i];
            }

            fairWeights[x] = sum;
        }

        double fairWeightsSum = Arrays.stream(fairWeights).sum();
        for (int i = 0; i < fairWeights.length; i++) {
            fairWeights[i] /= fairWeightsSum;
        }

        double sumLag = 0;
        for (int i = 0; i < fairWeights.length; i++) {
            sumLag += fairWeights[i] * i;
        }

        lag = (int) sumLag;
    }

    private void filterLine(
            IntFunction<V> pixelGetter,
            BiConsumer<Integer, V> pixelSetter,
            int length
    ) {
        // setting up first N values
        ArrayList<V> buffer = new ArrayList<>(lag + length);

        for (int x = 0; x < systemOrder.steps; x++) {
            V sum = pixelGetter.apply(x).amplify(fairWeights[0]);

            for (int i = 1; i < fairWeights.length; i++) {
                sum = sum.plus(pixelGetter.apply(x - i).amplify(fairWeights[i]));
            }
            buffer.add(sum);
        }

        // regular filtering
        for (int x = systemOrder.steps; x < length + lag; x++) {
            V sum = pixelGetter.apply(x).amplify(aCoefficient);

            for (int i = 1; i < bCoefficients.length; i++) {
                sum = sum.minus(buffer.get(x - i).amplify(bCoefficients[i]));
            }

            buffer.add(sum);
        }

        for (int x = 0; x < length; x++) {
            pixelSetter.accept(x, buffer.get(x+lag));
        }
    }

    @Override
    public void filter(ReadableImage<V> image, EditableImage<V> output) {

        int width = output.getWidth();
        int height = output.getHeight();

        IntStream.range(0, width).forEach(x -> {
            filterLine(
                    y -> image.getPixel(x, height - 1 - y),
                    (y, v) -> output.setPixel(x, height - 1 - y, v),
                    height
            );
            filterLine(
                    y -> output.getPixel(x, y),
                    (y, v) -> output.setPixel(x, y, v),
                    height
            );
        });

        IntStream.range(0, height).forEach(y -> {
            filterLine(
                    x -> output.getPixel(x, y),
                    (x, v) -> output.setPixel(x, y, v),
                    width
            );
            filterLine(
                    x -> output.getPixel(width - 1 - x, y),
                    (x, v) -> output.setPixel(width - 1 - x, y, v),
                    width
            );
        });
    }
}
