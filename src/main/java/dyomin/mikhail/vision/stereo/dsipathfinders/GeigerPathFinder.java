package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Deprecated
public class GeigerPathFinder implements PathFinder {
    private final IntToDoubleFunction jumpCost;
    private final int maxJump;

    private static class Jump {
        public final int length;
        public final boolean vertical;

        private Jump(int length, boolean vertical) {
            this.length = length;
            this.vertical = vertical;
        }
    }

    public GeigerPathFinder(IntToDoubleFunction jumpCost, int maxJump) {
        this.jumpCost = jumpCost;
        this.maxJump = maxJump;
    }

    @Override
    public int[] findPath(ReadableImage<WrappedDouble> dsi) {
        final int n = dsi.getWidth();
        double[][] costs = Stream.generate(() -> new double[n]).limit(n).toArray(double[][]::new);
        Jump[][] jumps = Stream.generate(() -> new Jump[n]).limit(n).toArray(Jump[][]::new);

        for (int r = 0; r < maxJump; r++) {
            costs[0][r] = jumpCost.applyAsDouble(r) + dsi.getPixel(0, r).value;
            costs[r][0] = jumpCost.applyAsDouble(r) + dsi.getPixel(r, 0).value;
            jumps[0][r] = new Jump(-1, false);
            jumps[r][0] = new Jump(-1, false);
        }

        for (int l = 1; l < n; l++) {
            for (int r = 1; r < n; r++) {
                double costHere = dsi.getPixel(l, r).value;
                double minCost = costs[l - 1][r - 1] + costHere;
                int jumpLength = 0;
                boolean vertical = true;

                // searching for vertical jumps
                for (int j = 1; (r - j > 0) && (j < maxJump); j++) {
                    double cost = costs[l][r - j] + jumpCost.applyAsDouble(j) + costHere;
                    if (cost < minCost) {
                        minCost = cost;
                        jumpLength = j;
                    }
                }

                // searching for horizontal jumps
                for (int j = 1; (l - j > 0) && (j < maxJump); j++) {
                    double cost = costs[l - j][r] + jumpCost.applyAsDouble(j) + costHere;
                    if (cost < minCost) {
                        minCost = cost;
                        jumpLength = j;
                        vertical = false;
                    }
                }

                costs[l][r] = minCost;
                jumps[l][r] = new Jump(jumpLength, vertical);
            }
        }

        int[] result = IntStream.generate(()->-1).limit(n).toArray();

        double minStart = Double.POSITIVE_INFINITY;
        Jump firstJump = new Jump(0,false);

        for (int j = 0; j< maxJump; j++){
            double cost = costs[n-1][j] + jumpCost.applyAsDouble(j);
            if (cost < minStart){
                minStart = cost;
                firstJump = new Jump(j, true);
            }
            cost = costs[j][n-1] + jumpCost.applyAsDouble(j);
            if (cost < minStart){
                minStart = cost;
                firstJump = new Jump(j, false);
            }

        }

        int l = n-1; int r = n-1;
        if (firstJump.vertical){
            r -= firstJump.length;
        } else {
            l -= firstJump.length;
        }

        Jump jump = firstJump;
        while (jump != null && jump.length != -1){
            result[l] = r;

            if (jump.vertical){
                r-= jump.length+1;
                l-=1;
            } else {
                r -= 1;
                l -= jump.length+1;
            }

            jump = jumps[l][r];
        }

        return result;
    }
}
