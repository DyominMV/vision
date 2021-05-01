package dyomin.mikhail.vision.math.dsisolvers;

import java.util.Arrays;

public class DiagAmplifierSolver implements DsiSolver {
    private final DsiSolver delegate;

    public DiagAmplifierSolver(DsiSolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public int[] findWay(double[][] map) {
        double[][] newMap = Arrays.stream(map)
                .map(double[]::clone)
                .toArray(double[][]::new);

        int n = newMap.length;

        for (int l = 1; l < n - 1; l++) {
            for (int r = 1; r < n - 1; r++) {
                newMap[l][r] = (0
                        + map[l][r]
                        + map[l + 1][r + 1]
                        + map[l - 1][r - 1]
                ) * 2 - (0
                        + map[l - 1][r]
                        + map[l + 1][r]
                        + map[l - 1][r + 1]
                        + map[l][r + 1]
                        + map[l][r - 1]
                        + map[l + 1][r - 1]
                );
            }
        }

        return delegate.findWay(newMap);
    }
}
