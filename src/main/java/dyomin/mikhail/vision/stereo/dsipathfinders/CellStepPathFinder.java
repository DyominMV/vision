package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.util.function.IntToDoubleFunction;

public class CellStepPathFinder extends DynamicProgrammingPathFinder {
    private final IntToDoubleFunction jumpCostFunction;

    public CellStepPathFinder(IntToDoubleFunction jumpCostFunction) {
        this.jumpCostFunction = jumpCostFunction;
    }

    @Override
    protected WeightedMove getWeightedMove(int l, int r, ReadableImage<WrappedDouble> dsi, LimitedResult result) {
        double weightHere = dsi.getPixel(l, r).value;
        WeightedMove bestMove = new WeightedMove(Double.POSITIVE_INFINITY, null);

        // check 3 closest points
        if (l > 0 && result.getWeight(l - 1, r) + weightHere < bestMove.weight) {
            bestMove = new WeightedMove(
                    result.getWeight(l - 1, r) + weightHere,
                    new Position(l - 1, r)
            );
        }
        if (r > 0 && result.getWeight(l, r - 1) + weightHere < bestMove.weight) {
            bestMove = new WeightedMove(
                    result.getWeight(l, r - 1) + weightHere,
                    new Position(l, r - 1)
            );
        }
        if (l > 0 && r > 0 && result.getWeight(l - 1, r - 1) + weightHere < bestMove.weight) {
            bestMove = new WeightedMove(
                    result.getWeight(l - 1, r - 1) + weightHere,
                    new Position(l - 1, r - 1)
            );
        }

        //check jumps

        if (r > 0) {
            for (int j = 0; j < l - 1; j++) {
                double weight = result.getWeight(l - j - 1, r - 1) +
                        weightHere +
                        jumpCostFunction.applyAsDouble(j);

                if (weight < bestMove.weight) {
                    bestMove = new WeightedMove(weight,new Position(l - j - 1, r - 1));
                }
            }
        }

        if (l > 0) {
            for (int j = 0; j < r - 1; j++) {
                double weight = result.getWeight(l - 1, r - j - 1) +
                        weightHere +
                        jumpCostFunction.applyAsDouble(j);

                if (weight < bestMove.weight) {
                    bestMove = new WeightedMove(weight, new Position(l - 1, r - j - 1));
                }

            }
        }

        if (bestMove.weight == Double.POSITIVE_INFINITY){
            bestMove = new WeightedMove(weightHere, null);
        }

        return bestMove;
    }
}
