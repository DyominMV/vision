package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Deprecated
public class SimpleGraphPathFinder implements PathFinder {

    private enum WalkDirection {
        TOP, LEFT, TOP_LEFT, HERE
    }

    @Override
    public int[] findPath(ReadableImage<WrappedDouble> dsi) {

        final int n = dsi.getWidth();

        double[][] routeWeights = Stream
                .generate(() -> new double[n])
                .limit(n)
                .toArray(double[][]::new);

        WalkDirection[][] directions = Stream
                .generate(() -> new WalkDirection[n])
                .limit(n)
                .toArray(WalkDirection[][]::new);

        routeWeights[0][0] = dsi.getPixel(0,0).value;
        directions[0][0] = WalkDirection.HERE;

        for (int i = 1; i < n; i++) {
            routeWeights[i][0] = dsi.getPixel(i,0).value + routeWeights[i - 1][0];
            routeWeights[0][i] = dsi.getPixel(0,i).value + routeWeights[0][i - 1];
            directions[i][0] = WalkDirection.TOP;
            directions[0][i] = WalkDirection.LEFT;
        }

        for (int l = 1; l < n; l++) {
            for (int r = 1; r < n; r++) {
                double topWeight = routeWeights[l - 1][r];
                double leftWeight = routeWeights[l][r - 1];
                double topLeftWeight = routeWeights[l-1][r-1];

                double min = DoubleStream.of(topWeight, leftWeight, topLeftWeight).min().getAsDouble();

                routeWeights[l][r] = dsi.getPixel(l,r).value + min;
                if (topWeight == min) {
                    directions[l][r] = WalkDirection.TOP;
                } else if (leftWeight == min) {
                    directions[l][r] = WalkDirection.LEFT;
                } else if (topLeftWeight == min) {
                    directions[l][r] = WalkDirection.TOP_LEFT;
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
                case TOP_LEFT:
                    r--;
                    l--;
                    break;
            }
        }

        return result;
    }
}
