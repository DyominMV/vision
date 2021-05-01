package dyomin.mikhail.vision.math.dsisolvers;

import java.util.function.IntToDoubleFunction;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SolverWithOcclusions implements DsiSolver {

    private enum WalkDirection {
        TOP, LEFT
    }

    private static class Movement {
        public final int length;
        public final WalkDirection direction;

        private Movement(int length, WalkDirection direction) {
            this.length = length;
            this.direction = direction;
        }
    }

    private final IntToDoubleFunction jumpWeight;

    public SolverWithOcclusions(IntToDoubleFunction jumpWeight) {
        this.jumpWeight = jumpWeight;
    }

    @Override
    public int[] findWay(double[][] map) {
        int n = map.length;

        double[][] routeWeights = Stream
                .generate(() -> new double[n])
                .limit(n)
                .toArray(double[][]::new);

        Movement[][] movements = Stream
                .generate(() -> new Movement[n])
                .limit(n)
                .toArray(Movement[][]::new);

        routeWeights[0][0] = map[0][0];
        movements[0][0] = new Movement(0, null);

        for (int l = 0; l < n; l++) {
            for (int r = 0; r < n; r++) {
                if (l == 0 && r == 0) continue;

                Movement bestMovement = new Movement(0, WalkDirection.TOP);
                double bestWeight = Double.POSITIVE_INFINITY;

                // check left weights
                for (int k = 1; k <= l; k++) {
                    double weight = jumpWeight.applyAsDouble(k) + routeWeights[l - k][r];

                    if (weight < bestWeight) {
                        bestWeight = weight;
                        bestMovement = new Movement(k, WalkDirection.TOP);
                    }
                }

                // check top weights
                for (int k = 1; k <= r; k++) {
                    double weight = jumpWeight.applyAsDouble(k) + routeWeights[l][r - k];

                    if (weight < bestWeight) {
                        bestWeight = weight;
                        bestMovement = new Movement(k, WalkDirection.LEFT);
                    }
                }

                routeWeights[l][r] = map[l][r] + bestWeight;
                movements[l][r] = bestMovement;
            }
        }

        int[] result = IntStream.generate(() -> (-1)).limit(n).toArray();

        int l = n - 1;
        int r = n - 1;

        while (movements[l][r].length != 0) {
            result[l] = r;

            switch (movements[l][r].direction) {
                case TOP:
                    l -= movements[l][r].length;
                    break;
                case LEFT:
                    r -= movements[l][r].length;
                    break;
            }
        }

        return result;
    }
}
