package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Deprecated
public class PiecewiseConstantPathFinder implements PathFinder {
    public interface JumpCost{
        double getCost(double edgeWeight, int jumpLength);
    }

    private final JumpCost jumpCost;

    public PiecewiseConstantPathFinder(JumpCost jumpCost) {
        this.jumpCost = jumpCost;
    }

    @Override
    public int[] findPath(ReadableImage<WrappedDouble> dsi) {
        int n = dsi.getWidth();
        double[][] costs = Stream
                .generate(() ->
                        DoubleStream.generate(() -> 0).limit(n).toArray()
                )
                .limit(n)
                .toArray(double[][]::new);

        int[][] jumps = Stream
                .generate(()->
                        IntStream.generate(()->0).limit(n).toArray()
                )
                .limit(n)
                .toArray(int[][]::new);

        for (int r = 0 ; r< n; r++){
            costs[0][r] = jumpCost.getCost(dsi.getPixel(0, r).value, r);
        }

        for (int r = 0 ; r< n; r++){
            jumps[0][r] = -1;
        }

        // left to right propagation
        for (int l = 1; l < n; l++){
            for (int r = 0; r < n; r++) {
                double costHere = dsi.getPixel(l,r).value;

                double minCost = Double.POSITIVE_INFINITY;
                int minJump = 0;

                for (int j = 0; j <= r; j++){
                    double cost = costs[l-1][j] + jumpCost.getCost(costHere, Math.abs(r-j-1));

                    if (cost < minCost) {
                        minCost = cost;
                        minJump = j;
                    }
                }
                costs[l][r] = minCost;
                jumps[l][r] = minJump;
            }
        }

        int lastJump = 0;

        {
            double minCost = Double.POSITIVE_INFINITY;

            for (int j = 0; j < n; j++){
                double cost = costs[n-1][j];

                if (cost < minCost) {
                    minCost = cost;
                    lastJump = j;
                }
            }
        }

        // right to left propagation
        int[] result = new int[n];
        result[n-1] = lastJump;

        for (int i = 1; i< n; i++){
            result[n-i-1] = jumps[n-i][result[n-i]];
        }

        return result;
    }
}
