package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.images.MatrixImage;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.math.dsisolvers.DsiSolver;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DsiStereo<V extends Vector<V>> implements LineByLineStereo<V> {

    private final DsiSolver solver;
    private final ToDoubleBiFunction<V, V> metric;

    public DsiStereo(DsiSolver solver, ToDoubleBiFunction<V, V> metric) {
        this.solver = solver;
        this.metric = metric;
    }

    public ReadableImage<WrappedDouble> getDsi(ReadableImage<V> left, ReadableImage<V> right, int y) {
        MatrixImage<WrappedDouble> result = new MatrixImage<>(left.getWidth(), right.getWidth());

        List<V> lefts = left.getRow(y).collect(Collectors.toList());
        List<V> rights = right.getRow(y).collect(Collectors.toList());

        IntStream.range(0, lefts.size()).parallel().forEach(l ->
                IntStream.range(0, rights.size()).parallel().forEach(r ->
                        result.setPixel(l, r, new WrappedDouble(
                                metric.applyAsDouble(lefts.get(l), rights.get(r))
                        ))
                )
        );

        return result;
    }

    private double[][] buildDsi(List<V> left, List<V> right) {
        double[][] result = new double[left.size()][];

        for (int l = 0; l < result.length; l++) {
            result[l] = new double[right.size()];

            for (int r = 0; r < result[l].length; r++) {
                result[l][r] = metric.applyAsDouble(left.get(l), right.get(r));
            }
        }

        return result;
    }

    @Override
    public List<Disparity> getDisparities(List<V> left, List<V> right) {
        int[] solution = solver.findWay(
                buildDsi(left, right)
        );

        for (int i = 0; i < solution.length; i++) {
            if (solution[i] >= 0) solution[i] = Math.abs(i - solution[i]);
        }

        return Arrays.stream(solution)
                .mapToObj(s -> s < 0 ? Disparity.NULL() : new Disparity(s))
                .collect(Collectors.toList());
    }
}
