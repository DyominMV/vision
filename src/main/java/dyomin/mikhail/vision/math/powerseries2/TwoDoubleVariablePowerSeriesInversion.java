package dyomin.mikhail.vision.math.powerseries2;

import Jama.Matrix;

public class TwoDoubleVariablePowerSeriesInversion {

    public final double xLimit;
    public final double yLimit;

    public final TwoDoubleVariablePowerSeries inputX;
    public final TwoDoubleVariablePowerSeries inputY;
    public final TwoDoubleVariablePowerSeries outputX;
    public final TwoDoubleVariablePowerSeries outputY;

    public TwoDoubleVariablePowerSeriesInversion(
            TwoDoubleVariablePowerSeries inputX, TwoDoubleVariablePowerSeries inputY,
            double xLimit, double yLimit,
            int maxPowerX, int maxPowerY
    ) {
        this.xLimit = xLimit;
        this.yLimit = yLimit;

        this.inputX = inputX;
        this.inputY = inputY;

        TwoDoubleVariablePowerSeries[][] powersOfInputs =
                new TwoDoubleVariablePowerSeries[maxPowerX * 2][maxPowerY * 2];
        // fill the first line
        powersOfInputs[0][0] = new TwoDoubleVariablePowerSeries(new double[][]{{1}});

        for (int x = 1; x < maxPowerX * 2; x++) {
            powersOfInputs[x][0] = powersOfInputs[x - 1][0].multiply(inputX);
        }

        // fill other lines
        for (int y = 1; y < maxPowerY * 2; y++) {
            powersOfInputs[0][y] = powersOfInputs[0][y - 1].multiply(inputY);

            for (int x = 1; x < maxPowerX * 2; x++) {
                powersOfInputs[x][y] = powersOfInputs[x - 1][y].multiply(inputX);
            }
        }

        // fill gradients
        double[] gradientVectorX = new double[maxPowerX * maxPowerY];
        double[] gradientVectorY = new double[maxPowerX * maxPowerY];
        for (int x = 0; x < maxPowerX; x++) {
            for (int y = 0; y < maxPowerY; y++) {
                gradientVectorX[y + x * maxPowerY] = -2 *
                        powersOfInputs[x][y].moveRightX()
                                .integral(-xLimit, xLimit, -yLimit, yLimit);

                gradientVectorY[y + x * maxPowerY] = -2 *
                        powersOfInputs[x][y].moveRightY()
                                .integral(-xLimit, xLimit, -yLimit, yLimit);
            }
        }

        // fill Hessian
        double[][] powersOfInputsIntegrated = new double[maxPowerX * 2][maxPowerY * 2];
        for (int x = 0; x < maxPowerX * 2; x++) {
            for (int y = 0; y < maxPowerY * 2; y++) {
                powersOfInputsIntegrated[x][y] = powersOfInputs[x][y].integral(-xLimit, xLimit, -yLimit, yLimit) * 2;
            }
        }

        double[][] hessian = new double[maxPowerX * maxPowerY][maxPowerX * maxPowerY];
        for (int x1 = 0; x1 < maxPowerX; x1++) {
            for (int y1 = 0; y1 < maxPowerY; y1++) {
                for (int x2 = 0; x2 < maxPowerX; x2++) {
                    System.arraycopy(
                            powersOfInputsIntegrated[x1 + x2],
                            y1,
                            hessian[y1 + x1 * maxPowerY],
                            x2 * maxPowerY,
                            maxPowerY
                    );
                }
            }
        }

        Matrix inverseHessian = new Matrix(hessian).inverse();

        // calculate result
        double[] resultX = new Matrix(new double[][]{gradientVectorX})
                .times(inverseHessian).times(-1)
                .getArray()[0];
        double[] resultY = new Matrix(new double[][]{gradientVectorY})
                .times(inverseHessian).times(-1)
                .getArray()[0];

        double[][] resultCoefficientsX = new double[maxPowerX][maxPowerY];
        double[][] resultCoefficientsY = new double[maxPowerX][maxPowerY];
        for (int x = 0; x < maxPowerX; x++) {
            for (int y = 0; y < maxPowerY; y++) {
                resultCoefficientsX[x][y] = resultX[y + x * maxPowerY];
                resultCoefficientsY[x][y] = resultY[y + x * maxPowerY];
            }
        }

        outputX = new TwoDoubleVariablePowerSeries(resultCoefficientsX);
        outputY = new TwoDoubleVariablePowerSeries(resultCoefficientsY);
    }

    public double quality() {
        TwoDoubleVariablePowerSeries one = new TwoDoubleVariablePowerSeries(new double[][]{{1}});

        TwoDoubleVariablePowerSeries xSquare =
                outputX.substitute(inputX, inputY).minus(one.moveRightX());
        xSquare = xSquare.multiply(xSquare);

        TwoDoubleVariablePowerSeries ySquare =
                outputY.substitute(inputX, inputY).minus(one.moveRightY());
        ySquare = ySquare.multiply(ySquare);

        return xSquare.integral(-xLimit, xLimit, -yLimit, yLimit) +
                ySquare.integral(-xLimit, xLimit, -yLimit, yLimit);
    }

}
