package dyomin.mikhail.vision.stereo;

import dyomin.mikhail.vision.math.dsisolvers.DsiSolver;
import dyomin.mikhail.vision.vectors.Disparity;
import dyomin.mikhail.vision.vectors.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DsiStereo<V extends Vector<V>> implements LineByLineStereo<V>{

    private final DsiSolver solver;

    public DsiStereo(DsiSolver solver) {
        this.solver = solver;
    }

    private double[][] buildDsi(List<V> left, List<V> right){
        double[][] result = new double[left.size()][];

        for (int l = 0; l< result.length; l++){
            result[l] = new double[right.size()];

            for (int r = 0; r< result[l].length; r++){
                result[l][r] = left.get(l).minus(right.get(r)).length();
            }
        }

        return  result;
    }

    @Override
    public List<Disparity> getDisparities(List<V> left, List<V> right) {
        int[] solution = solver.findWay(
                buildDsi(left, right)
        );

        for (int i=0 ; i< solution.length; i++){
            solution[i] = Math.abs(i - solution[i]);
        }

        return Arrays.stream(solution).mapToObj(Disparity::new).collect(Collectors.toList());
    }
}
