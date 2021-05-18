package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.stereo.dsi.DsiFactory;
import dyomin.mikhail.vision.stereo.dsipathfinders.PathFinder;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

public class DsiStereo<V extends Vector<V>> implements LineByLineStereo<V> {

    private final PathFinder pathFinder;
    private final DsiFactory<V> dsiFactory;

    public DsiStereo(PathFinder pathFinder, ToDoubleBiFunction<V, V> metric) {
        this.pathFinder = pathFinder;
        this.dsiFactory = new DsiFactory<>(metric);
    }

    @Override
    public List<Disparity> getDisparities(List<V> left, List<V> right) {
        int[] solution = pathFinder.findPath(
                dsiFactory.getMatrixDsi(left,right)
        );

        for (int i = 0; i < solution.length; i++) {
            if (solution[i] >= 0) solution[i] = Math.abs(i - solution[i]);
        }

        return Arrays.stream(solution)
                .mapToObj(s -> s < 0 ? Disparity.NULL() : new Disparity(s))
                .collect(Collectors.toList());
    }
}
