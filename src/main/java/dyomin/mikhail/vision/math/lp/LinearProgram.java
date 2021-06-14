package dyomin.mikhail.vision.math.lp;

import Jama.Matrix;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// argmax cT * x where Ax = b , x >= 0, c >= 0
public class LinearProgram {

    private final Matrix a;
    private final Matrix b;
    private final Matrix c;

    public LinearProgram(double[][] a, double[] b, double[] c) {
        this.a = new Matrix(a);
        this.b = new Matrix(new double[][]{b}).transpose();
        this.c = new Matrix(new double[][]{c}).transpose();
    }

    public double[][] getA() {
        return a.copy().getArray();
    }

    public double[] getB() {
        return b.getColumnPackedCopy();
    }

    public double[] getC() {
        return c.getColumnPackedCopy();
    }

    public LinearProgram addConstraintString(double[] aI, double bI) {
        return new LinearProgram(
                Stream.concat(Arrays.stream(getA()), Stream.of(aI)).toArray(double[][]::new),
                DoubleStream.concat(Arrays.stream(getB()), DoubleStream.of(bI)).toArray(),
                getC()
        );
    }

    public LinearProgram fixVariable(int variableIndex, double value) {
        double[] nextRow = new double[a.getColumnDimension()];
        nextRow[variableIndex] = 1;
        return addConstraintString(nextRow, value);
    }

    public double targetValue(double[] x) {
        return new Matrix(new double[][]{x}).times(c).get(0, 0);
    }

    private int[] nonPivot(int[] pivot) {
        return IntStream.range(0, this.a.getColumnDimension())
                .filter(i -> Arrays.binarySearch(pivot, i) < 0)
                .toArray();
    }

    public int[] nextPivot(int[] prevPivot) {
        if (prevPivot.length != b.getRowDimension()) {
            throw new RuntimeException("Pivot coordinates count must be equal to constraints count");
        }

        int[] nonPivot = nonPivot(prevPivot);

        Matrix pivotCols = a.getMatrix(0, a.getRowDimension() - 1, prevPivot);

        if (Math.abs(pivotCols.det()) <= 1E-12) {
            return prevPivot;
        }

        Matrix inversePivotCols = pivotCols.inverse();

        Matrix pivotC = c.getMatrix(prevPivot, 0, 0);

        Matrix enteringVarPicker = pivotC.transpose()
                .times(inversePivotCols)
                .times(a)
                .transpose().minus(c);

        int entering = Arrays.stream(nonPivot)
                .boxed()
                .min(Comparator.comparingDouble(i -> enteringVarPicker.get(i, 0)))
                .orElse(-1);

        if (entering < 0) {
            return prevPivot;
        }

        if (enteringVarPicker.get(entering, 0) >= 0) {
            return prevPivot;
        }

        Matrix numerator = inversePivotCols.times(b);
        Matrix denominator = inversePivotCols.times(
                a.getMatrix(0, a.getRowDimension() - 1, entering, entering)
        );

        int leaving = IntStream.range(0, prevPivot.length)
                .boxed()
                .filter(i-> {
                    double numeratorI = numerator.get(i, 0);
                    double denominatorI = denominator.get(i, 0);

                    return !(Math.abs(denominatorI) <= 1E-12) &&
                            !(Math.abs(numeratorI) <= 1E-12) &&
                            !(numeratorI / denominatorI < 0);
                })
                .min(Comparator.comparingDouble(i ->
                        numerator.get(i, 0) / denominator.get(i, 0))
                )
                .map(i -> prevPivot[i])
                .orElse(-1);

        if (leaving < 0) {
            return prevPivot;
        }

        return IntStream
                .concat(
                        IntStream.of(entering),
                        Arrays.stream(prevPivot).filter(i -> i != leaving)
                )
                .sorted()
                .toArray();
    }

    public double[] solve(int[] basicPivot) {
        int[] pivot = basicPivot;
        int[] nextPivot = nextPivot(pivot);

        while (!Arrays.equals(pivot, nextPivot)) {
            pivot = nextPivot;
            nextPivot = nextPivot(pivot);
        }

        Matrix pivotCols = a.getMatrix(0, a.getRowDimension() - 1, pivot);
        Matrix pivotSolution = pivotCols.solve(b);

        int[] finalPivot = pivot;
        return IntStream.range(0, c.getRowDimension())
                .mapToDouble(i -> {
                    int index = Arrays.binarySearch(finalPivot, i);

                    if (index < 0) {
                        return 0;
                    }

                    return pivotSolution.get(index, 0);
                })
                .toArray();
    }
}
