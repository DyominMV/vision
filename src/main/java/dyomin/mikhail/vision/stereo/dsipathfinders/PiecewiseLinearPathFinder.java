package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Deprecated
public class PiecewiseLinearPathFinder implements PathFinder{
    private final DoubleUnaryOperator gradientCost;

    public PiecewiseLinearPathFinder(DoubleUnaryOperator gradientCost) {
        this.gradientCost = gradientCost;
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
            costs[0][r] = dsi.getPixel(0, r).value + gradientCost.applyAsDouble(r);
        }

        for (int r = 0 ; r< n; r++){
            jumps[0][r] = -1;
        }

        // left to right propagation
        for (int l = 1; l < n; l++){
            for (int r = 0; r < n; r++) {

                // horizontal jump cost is dsi.getPixel(l,r).value

                double minCost = costs[l-1][r] + costs[l][r] + gradientCost.applyAsDouble(1);
                int minJump = r;

                double costSum = dsi.getPixel(l,r-1).value;
                int jumpLen = 1;
                for (int j = r-1; j >= 0; j--){
                    double cost = costs[l-1][j] + costSum / jumpLen + gradientCost.applyAsDouble(Math.abs(jumpLen-1));

                    if (cost < minCost) {
                        minCost = cost;
                        minJump = j;
                    }

                    costSum += dsi.getPixel(l,j-1).value;
                    jumpLen++;
                }
                costs[l][r] = minCost;
                jumps[l][r] = minJump;
            }
        }

        int lastJump = n-1;

        {
            double minCost = Double.POSITIVE_INFINITY;

            for (int j = 0; j < n; j++){
                double cost = costs[n-1][j] + gradientCost.applyAsDouble(Math.abs(n-1-j));

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
