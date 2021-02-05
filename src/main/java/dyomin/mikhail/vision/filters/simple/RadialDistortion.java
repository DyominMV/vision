package dyomin.mikhail.vision.filters.simple;

import dyomin.mikhail.vision.vectors.Direction;
import dyomin.mikhail.vision.vectors.Vector;

public class RadialDistortion<V extends Vector<V>> extends Distortion<V> {
    private final double[] coefficients;
    private final double centerX;
    private final double centerY;

    public RadialDistortion(double centerX, double centerY, double... coefficients) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.coefficients = coefficients;
    }

    @Override
    protected Direction distort(int x, int y) {
        double dx = x - centerX;
        double dy = y - centerY;
        double rSquared = dx*dx + dy*dy;
        double rCoefficient = findPolynomialValue(rSquared);

        return new Direction(
                dx*rCoefficient + centerX,
                dy*rCoefficient + centerY
        );
    }

    private double findPolynomialValue(double rSquared){
        double sum = 0;

        for (int i = coefficients.length -1; i>= 0; i--){
            sum += coefficients[i];
            sum *= rSquared;
        }

        return sum +1;
    }

}
