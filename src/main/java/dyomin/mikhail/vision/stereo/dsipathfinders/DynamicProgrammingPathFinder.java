package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class DynamicProgrammingPathFinder implements PathFinder {

    protected static class Position {
        public final int left;
        public final int right;

        public Position(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    protected static class WeightedMove{
        public final double weight;
        public final Position move;

        public WeightedMove(double weight, Position move) {
            this.weight = weight;
            this.move = move;
        }
    }

    protected static class LimitedResult{
        private final WeightedMove[][] result;
        public final int limitL;
        public final int limitR;

        public LimitedResult(WeightedMove[][] result, int limitL, int limitR) {
            this.result = result;
            this.limitL = limitL;
            this.limitR = limitR;
        }

        public double getWeight(int l, int r){
            if (limitL < l || limitR < r || (limitL == l && limitR == r)){
                throw new RuntimeException("Desired move is out of bounds!");
            }

            return result[l][r].weight;
        }
    }

    protected abstract WeightedMove getWeightedMove(int l, int r, ReadableImage<WrappedDouble> dsi, LimitedResult result);

    @Override
    public int[] findPath(ReadableImage<WrappedDouble> dsi) {
        final int n = dsi.getWidth();

        final WeightedMove[][] result = Stream.generate(()-> new WeightedMove[n])
                .limit(n)
                .toArray(WeightedMove[][]::new);

        for (int l = 0; l< n; l++){
            for (int r = 0; r< n; r++){
                result[l][r] = getWeightedMove(l,r, dsi, new LimitedResult(result,l,r));
            }
        }

        List<Position> path = new ArrayList<>(n);
        Position currentPosition = new Position(n-1, n-1);
        while (currentPosition != null){
            path.add(currentPosition);
            currentPosition = result[currentPosition.left][currentPosition.right].move;
        }

        Position lastPos = path.get(path.size()-1);
        if (lastPos.left != 0 && lastPos.right != 0){
            path.add(new Position(0,0));
        }

        int[] resultPath = IntStream.generate(()->-1).limit(n).toArray();

        for (Position p : path){
            resultPath[p.left] = p.right;
        }

        return resultPath;
    }


}
