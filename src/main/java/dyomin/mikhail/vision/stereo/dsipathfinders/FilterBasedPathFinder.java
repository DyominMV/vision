package dyomin.mikhail.vision.stereo.dsipathfinders;

import dyomin.mikhail.vision.filters.ImageFilter;
import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.detector.LocalMinimum;
import dyomin.mikhail.vision.filters.simple.detector.morphological.MorphoTransform;
import dyomin.mikhail.vision.images.ReadableImage;
import dyomin.mikhail.vision.math.graph.DirectedGraph;
import dyomin.mikhail.vision.vectors.WrappedBoolean;
import dyomin.mikhail.vision.vectors.WrappedDouble;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FilterBasedPathFinder implements PathFinder {

    public FilterBasedPathFinder(DoubleUnaryOperator jumpCostFunction) {
        this.jumpCostFunction = jumpCostFunction;
    }

    private static final class Point {
        public final int x;
        public final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class ConnectionComponent {
        private final List<Point> points = new ArrayList<>();
        private double weightsSum = 0;
        private Point topLeft = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        private Point bottomRight = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        public void addPoint(Point p, double weight) {
            points.add(p);

            topLeft = new Point(Math.min(p.x, topLeft.x), Math.min(p.y, topLeft.y));
            bottomRight = new Point(Math.max(p.x, bottomRight.x), Math.max(p.y, bottomRight.y));

            weightsSum += weight;
        }

        public double getWeight() {
            return points.size() > 0 ? weightsSum / points.size(): 0;
        }

        public Stream<Point> getPoints() {
            return points.stream();
        }

        public double distanceTo(ConnectionComponent other) {
            Point topLeft = this.bottomRight;
            Point bottomRight = other.topLeft;

            double xDiff = bottomRight.x - topLeft.x;
            double yDiff = bottomRight.y - topLeft.y;

            return xDiff >= 0 && yDiff >= 0
                    ? Math.sqrt(xDiff * xDiff + yDiff * yDiff)
                    : Double.POSITIVE_INFINITY;
        }
    }


    private static final class DisjointIntSet {
        private final Map<Integer, Integer> parents = new HashMap<>();

        public void makeSet(int i) {
            if (find(i) == null) {
                parents.put(i, i);
            }
        }

        public Integer find(int i) {
            Integer parent = parents.get(i);

            if (null == parent) {
                return null;
            } else if (parent.equals(i)) {
                return i;
            } else {
                return find(parent);
            }
        }

        public void union(int i1, int i2) {
            Integer parent1 = find(i1);
            if (parent1 == null) {
                makeSet(i1);
                parent1 = i1;
            }

            if (i1 == i2) {
                return;
            }

            Integer parent2 = find(i2);
            if (parent2 == null) {
                makeSet(i2);
                parent2 = i2;
            }

            parents.put(parent1, parent2);
        }
    }

    private static final ImageFilter<WrappedDouble, WrappedBoolean> PART_EXTRACTOR =
            new PseudoGaussianBlur<WrappedDouble>(1.2, SystemOrder.FIVE)
                    .compose(new LocalMinimum(LocalMinimum.Direction.DIAGONAL_R))
                    .compose(new MorphoTransform(MorphoTransform.TransformKind.ERODE, -1, -1, 0, 0, 1, 1))
                    .compose(new MorphoTransform(MorphoTransform.TransformKind.DILATE, -1, -1, 0, 0, 1, 1));

    private final DoubleUnaryOperator jumpCostFunction;

    @Override
    public int[] findPath(ReadableImage<WrappedDouble> dsi) {
        ReadableImage<WrappedBoolean> filteredDsi = dsi.applyFilter(PART_EXTRACTOR);

        int n = dsi.getWidth();

        // labeling phase
        int[][] pixelLabels = Stream.generate(() -> new int[n]).limit(n).toArray(int[][]::new);

        int nextLabel = 1;

        DisjointIntSet labels = new DisjointIntSet();

        // setting label for 0,0
        if (filteredDsi.getPixel(0, 0).value) {
            pixelLabels[0][0] = nextLabel;
            labels.makeSet(nextLabel);
            nextLabel++;
        }

        // labels for x,0
        for (int x = 1; x < n; x++) {
            if (filteredDsi.getPixel(x, 0).value) {
                if (pixelLabels[x - 1][0] != 0) {
                    pixelLabels[x][0] = pixelLabels[x - 1][0];
                } else {
                    pixelLabels[x][0] = nextLabel;
                    labels.makeSet(nextLabel);
                    nextLabel++;
                }
            }
        }

        // labels for 0,y
        for (int y = 1; y < n; y++) {
            if (filteredDsi.getPixel(0, y).value) {
                if (pixelLabels[0][y - 1] != 0) {
                    pixelLabels[0][y] = pixelLabels[0][y - 1];
                } else {
                    pixelLabels[0][y] = nextLabel;
                    labels.makeSet(nextLabel);
                    nextLabel++;
                }
            }
        }

        // least labels
        for (int x = 1; x < n; x++) {
            for (int y = 1; y < n; y++) {
                if (filteredDsi.getPixel(x, y).value) {
                    if (pixelLabels[x - 1][y] != 0) {
                        pixelLabels[x][y] = pixelLabels[x - 1][y];
                    }
                    if (pixelLabels[x][y - 1] != 0) {
                        if (pixelLabels[x][y] != 0) {
                            labels.union(pixelLabels[x][y], pixelLabels[x][y - 1]);
                        } else {
                            pixelLabels[x][y] = pixelLabels[x][y - 1];
                        }
                    }
                    if (pixelLabels[x - 1][y - 1] != 0) {
                        if (pixelLabels[x][y] != 0) {
                            labels.union(pixelLabels[x][y], pixelLabels[x - 1][y - 1]);
                        } else {
                            pixelLabels[x][y] = pixelLabels[x - 1][y - 1];
                        }
                    }

                    if (pixelLabels[x][y] == 0) {
                        pixelLabels[x][y] = nextLabel;
                        labels.makeSet(nextLabel);
                        nextLabel++;
                    }
                }
            }
        }

        // final labeling
        int[] trueLabels = IntStream.range(1, nextLabel)
                .map(l -> Optional.ofNullable(labels.find(l)).orElse(0))
                .toArray();

        Map<Integer, ConnectionComponent> connectionComponents = new HashMap<>();
        Arrays.stream(trueLabels).forEach(label -> connectionComponents.put(label, new ConnectionComponent()));


        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                int label = pixelLabels[x][y];

                if (label == 0) {
                    continue;
                }

                label = trueLabels[label - 1];
                connectionComponents.get(label).addPoint(new Point(x, y), dsi.getPixel(x, y).value);
            }
        }

        // components picking phase
        ConnectionComponent begin = new ConnectionComponent();
        begin.addPoint(new Point(-1, -1), 0);

        ConnectionComponent end = new ConnectionComponent();
        end.addPoint(new Point(n, n), 0);

        DirectedGraph<ConnectionComponent> componentsDirectedGraph = new DirectedGraph<>(
                Stream.concat(connectionComponents.values().stream(), Stream.of(begin, end)).collect(Collectors.toList()),
                ConnectionComponent::distanceTo
        );

        List<ConnectionComponent> path = componentsDirectedGraph.shortestPath(begin, end, ConnectionComponent::getWeight);
        List<ConnectionComponent> selectedComponents = path
                .stream()
                .skip(1)
                .limit(Math.max(0, path.size()-2))
                .collect(Collectors.toList());

        int[] result = IntStream.generate(() -> -1).limit(n).toArray();
        selectedComponents.stream().flatMap(ConnectionComponent::getPoints).forEach(point ->
                result[point.x] = point.y
        );

        return result;
    }
}
