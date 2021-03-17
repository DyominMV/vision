package dyomin.mikhail.vision.vectors;

public interface Vector<V extends Vector<V>> {
    V plus(V other);

    V minus(V other);

    V amplify(double coefficient);

    double length();

    RGB visualize();
}
