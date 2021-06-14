package dyomin.mikhail.vision.math.graph;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import dyomin.mikhail.vision.math.lp.LinearProgram;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UndirectedGraph<Vertex> {
    private final Matrix adjacencyMatrix;
    private final Map<Vertex, Integer> indices;
    private final List<Vertex> storedVertices;

    public UndirectedGraph(List<Vertex> vertices, Matrix adjacencyMatrix) {
        storedVertices = new ArrayList<>(vertices);
        indices = IntStream.range(0, storedVertices.size())
                .boxed()
                .collect(Collectors.toMap(
                        storedVertices::get,
                        Function.identity()
                ));
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public UndirectedGraph(Collection<Vertex> vertices, BiPredicate<Vertex, Vertex> adjustment) {
        storedVertices = new ArrayList<>(vertices);

        indices = IntStream.range(0, storedVertices.size())
                .boxed()
                .collect(Collectors.toMap(
                        storedVertices::get,
                        Function.identity()
                ));

        this.adjacencyMatrix = new Matrix(storedVertices.size(), storedVertices.size());
        IntStream.range(0, storedVertices.size()).forEach(x ->
                IntStream.range(x, storedVertices.size())
                        .forEach(y -> {
                            adjacencyMatrix.set(x, y,
                                    adjustment.test(storedVertices.get(x), storedVertices.get(y)) ? 1 : 0
                            );
                            adjacencyMatrix.set(y, x,
                                    adjustment.test(storedVertices.get(x), storedVertices.get(y)) ? 1 : 0
                            );
                        })
        );
    }

    private static Matrix diagonal(double[] values) {
        Matrix result = Matrix.identity(values.length, values.length);
        IntStream.range(0, values.length).forEach(i -> result.set(i, i, values[i]));
        return result;
    }

    private static Matrix row(double[] values) {
        return new Matrix(new double[][]{values});
    }

    private static Matrix col(double[] values) {
        return new Matrix(Arrays.stream(values).mapToObj(d -> new double[]{d}).toArray(double[][]::new));
    }

    public Matrix laplacianMatrix(boolean signless) {
        Matrix ones = col(DoubleStream.generate(() -> 1).limit(storedVertices.size()).toArray());

        Matrix degrees = diagonal(
                adjacencyMatrix.times(ones).getColumnPackedCopy()
        );

        return signless
                ? degrees.plus(adjacencyMatrix)
                : degrees.minus(adjacencyMatrix);
    }

    public Matrix laplacianMatrix() {
        return laplacianMatrix(false);
    }

    public int verticesCount() {
        return storedVertices.size();
    }

    public List<UndirectedGraph<Vertex>> connectionComponents() {
        if (verticesCount() == 0) {
            return Collections.emptyList();
        }

        EigenvalueDecomposition decomposition = laplacianMatrix().eig();

        int componentsCount = (int) Arrays.stream(decomposition.getRealEigenvalues())
                .filter(d -> Math.abs(d) < 1E-12)
                .count();

        return IntStream.range(0, componentsCount).mapToObj(i -> {
            int[] markedVertices = Arrays
                    .stream(decomposition.getV()
                            .getMatrix(0, verticesCount() - 1, i, i)
                            .getColumnPackedCopy()
                    )
                    .mapToInt(d -> Math.abs(d) <= 1E-12 ? 0 : 1)
                    .toArray();

            int[] markedIndices = IntStream
                    .range(0, verticesCount())
                    .filter(j -> markedVertices[j] != 0)
                    .toArray();

            return new UndirectedGraph<>(
                    Arrays.stream(markedIndices).mapToObj(storedVertices::get).collect(Collectors.toList()),
                    adjacencyMatrix.getMatrix(markedIndices, markedIndices)
            );
        }).collect(Collectors.toList());

    }

    public UndirectedGraph<Vertex> removeVertices(Collection<Vertex> vertices) {
        int[] leftIndices = IntStream.range(0, storedVertices.size())
                .filter(i -> !vertices.contains(storedVertices.get(i)))
                .toArray();

        return new UndirectedGraph<>(
                Arrays.stream(leftIndices).mapToObj(storedVertices::get).collect(Collectors.toList()),
                adjacencyMatrix.getMatrix(leftIndices, leftIndices)
        );
    }

    public List<Vertex> getMaxWeightIndependentSet(ToDoubleFunction<Vertex> weight) {
        return this.connectionComponents()
                .stream()
                .flatMap(graph -> graph
                        .getMaxWeightIndependentSet(weight, 0)
                        .stream()
                )
                .collect(Collectors.toList());
    }

    private List<Vertex> getMaxWeightIndependentSet(ToDoubleFunction<Vertex> weight, double minimalTotalWeight) {
        if (verticesCount() == 0) {
            return Collections.emptyList();
        }

        if (verticesCount() == 1) {
            return Collections.singletonList(storedVertices.get(0));
        }

        double[][] a = new Matrix(Stream.concat(
                Arrays.stream(laplacianMatrix(true).getArray()),
                Arrays.stream(Matrix.identity(verticesCount(), verticesCount()).getArray())
        ).toArray(double[][]::new))
                .transpose()
                .getArray();

        Matrix ones = col(DoubleStream.generate(() -> 1).limit(storedVertices.size()).toArray());

        double[] b = adjacencyMatrix.times(ones).getColumnPackedCopy();
        IntStream.range(0, b.length).forEach(i -> {
            if (b[i] > 0.1) {
                return;
            }

            b[i] = 1;
            a[i][i] = 1;
        });

        double[] c = DoubleStream
                .concat(
                        storedVertices.stream().mapToDouble(weight),
                        Arrays.stream(new double[storedVertices.size()])
                )
                .toArray();

        LinearProgram linearProgram = new LinearProgram(a, b, c);
        double[] solution = linearProgram.solve(IntStream.range(verticesCount(), verticesCount() * 2).toArray());
        double totalWeight = linearProgram.targetValue(solution);

        // check if branch is non-optimal
        if (totalWeight < minimalTotalWeight) {
            return Collections.emptyList();
        }

        // check if solution is found
        Optional<Integer> nonBooleanVar = IntStream.range(0, verticesCount())
                .filter(i -> !isBoolean(solution[i]))
                .boxed()
                .max(Comparator.comparingDouble(i -> b[i]));

        if (!nonBooleanVar.isPresent()) {
            return IntStream.range(0, verticesCount())
                    .filter(i -> solution[i] > 0.5)
                    .mapToObj(storedVertices::get)
                    .collect(Collectors.toList());
        }

        Vertex removedVertex = storedVertices.get(nonBooleanVar.get());

        // what if save vertex with non-boolean coefficient?
        Set<Vertex> verticesToRemove = getAdjustedVertices(removedVertex);
        verticesToRemove.add(removedVertex);

        UndirectedGraph<Vertex> graphWith = this.removeVertices(
                verticesToRemove
        );

        List<Vertex> resultWith = Stream.concat(
                graphWith.getMaxWeightIndependentSet(
                        weight,
                        minimalTotalWeight - weight.applyAsDouble(removedVertex)
                ).stream(),
                Stream.of(removedVertex)
        ).collect(Collectors.toList());
        double weightOfResultWith = resultWith.stream().mapToDouble(weight).sum();

        // what if delete vertex with non-boolean coefficient?
        UndirectedGraph<Vertex> graphWithout = this.removeVertices(
                Collections.singletonList(removedVertex)
        );

        List<Vertex> resultWithout = graphWithout.getMaxWeightIndependentSet(weight, weightOfResultWith);
        double weightOfResultWithout = resultWithout.stream().mapToDouble(weight).sum();

        // choose best one
        return weightOfResultWithout > weightOfResultWith
                ? resultWithout
                : resultWith;
    }

    private boolean isBoolean(double value) {
        return Math.abs(Math.round(value) - value) <= 1E-12 && value <= 1 + 1E-12;
    }

    public Set<Vertex> getAdjustedVertices(Vertex vertex) {
        double[] one = new double[verticesCount()];
        one[indices.get(vertex)] = 1;
        double[] markedVertices = row(one).times(adjacencyMatrix).getColumnPackedCopy();
        return IntStream.range(0, verticesCount())
                .filter(i -> markedVertices[i] > 0.5)
                .mapToObj(storedVertices::get)
                .collect(Collectors.toSet());
    }

}
