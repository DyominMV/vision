package dyomin.mikhail.vision.filters.gauss;

public enum SystemOrder {
    THREE(3), FOUR(4), FIVE(5);
    public final int steps;

    SystemOrder(int steps) {
        this.steps = steps;
    }
}
