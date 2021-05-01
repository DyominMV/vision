package dyomin.mikhail.vision.math.dsisolvers;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SimpleGraphSolver implements DsiSolver {

    private enum WalkDirection {
        TOP, LEFT, HERE
    }

    @Override
    public int[] findWay(double[][] map) {

        final int n = map.length;

        double[][] routeWeights = Stream
                .generate(() -> new double[n])
                .limit(n)
                .toArray(double[][]::new);

        WalkDirection[][] directions = Stream
                .generate(() -> new WalkDirection[n])
                .limit(n)
                .toArray(WalkDirection[][]::new);

        routeWeights[0][0] = map[0][0];
        directions[0][0] = WalkDirection.HERE;

        for (int i = 1; i < n; i++) {
            routeWeights[i][0] = map[i][0] + routeWeights[i - 1][0];
            routeWeights[0][i] = map[0][i] + routeWeights[0][i - 1];
            directions[i][0] = WalkDirection.TOP;
            directions[0][i] = WalkDirection.LEFT;
        }

        for (int l = 1; l < n; l++) {
            for (int r = 1; r < n; r++) {
                double topWeight = routeWeights[l - 1][r];
                double leftWeight = routeWeights[l][r - 1];

                double min = DoubleStream.of(topWeight, leftWeight).min().getAsDouble();

                routeWeights[l][r] = map[l][r] + min;
                if (topWeight == min) {
                    directions[l][r] = WalkDirection.TOP;
                } else if (leftWeight == min) {
                    directions[l][r] = WalkDirection.LEFT;
                }
            }
        }

        int[] result = IntStream.generate(() -> (-1)).limit(n).toArray();

        int l = n - 1;
        int r = n - 1;

        while (directions[l][r] != WalkDirection.HERE) {
            result[l] = r;

            switch (directions[l][r]) {
                case TOP:
                    l--;
                    break;
                case LEFT:
                    r--;
                    break;
            }
        }

        return result;
    }
}
