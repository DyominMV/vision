package dyomin.mikhail.vision.math.graph;

import Jama.Matrix;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class DirectedGraph<Vertex> {
    private final Matrix adjacencyMatrix;
    private final Map<Vertex, Integer> indices;
    private final List<Vertex> storedVertices;

    public DirectedGraph(Collection<Vertex> vertices, ToDoubleBiFunction<Vertex, Vertex> edgeWeight) {
        storedVertices = vertices.stream().distinct().collect(Collectors.toList());

        indices = IntStream.range(0, storedVertices.size())
                .boxed()
                .collect(Collectors.toMap(
                        storedVertices::get,
                        Function.identity()
                ));


        this.adjacencyMatrix = new Matrix(storedVertices.size(), storedVertices.size());
        IntStream.range(0, storedVertices.size()).forEach(x ->
                IntStream.range(0, storedVertices.size())
                        .forEach(y -> adjacencyMatrix.set(x, y,
                                edgeWeight.applyAsDouble(storedVertices.get(x), storedVertices.get(y))
                        ))
        );
    }

    public List<Vertex> verticesFrom(Vertex source) {
        Integer key = indices.get(source);

        if (null == key) {
            return Collections.emptyList();
        }

        return indicesFrom(key).mapToObj(storedVertices::get)
                .collect(Collectors.toList());
    }

    private IntStream indicesFrom(int sourceIndex) {
        return IntStream.range(0, storedVertices.size())
                .filter(i -> Double.isFinite(adjacencyMatrix.getArray()[sourceIndex][i]));
    }

    public List<Vertex> verticesTo(Vertex target) {
        Integer key = indices.get(target);

        if (null == key) {
            return Collections.emptyList();
        }

        return indicesTo(key)
                .mapToObj(storedVertices::get)
                .collect(Collectors.toList());
    }

    private IntStream indicesTo(int targetIndex) {
        return IntStream.range(0, storedVertices.size())
                .filter(i -> Double.isFinite(adjacencyMatrix.getArray()[i][targetIndex]));
    }

    public List<Vertex> shortestPath(Vertex source, Vertex target, ToDoubleFunction<Vertex> vertexWeight) {
        Integer sourceKey = indices.get(source);
        if (null == sourceKey) {
            return null;
        }

        Integer targetKey = indices.get(target);
        if (null == targetKey) {
            return null;
        }

        if (sourceKey.equals(targetKey)) {
            return Collections.emptyList();
        }

        int[] prev = IntStream.generate(() -> -1).limit(storedVertices.size()).toArray();
        double[] dist = DoubleStream.generate(() -> Double.POSITIVE_INFINITY).limit(storedVertices.size()).toArray();
        dist[sourceKey] = 0;

        Set<Integer> leftVertices = IntStream.range(0, storedVertices.size()).boxed().collect(Collectors.toSet());

        while (!leftVertices.isEmpty()) {
            int closest = leftVertices.stream().min(Comparator.comparingDouble(i -> dist[i])).get();

            if (closest == targetKey) {
                ArrayList<Integer> result = new ArrayList<>();
                int next = closest;

                while (next != -1) {
                    result.add(next);
                    next = prev[next];
                }

                Collections.reverse(result);

                return result.stream().map(storedVertices::get).collect(Collectors.toList());
            }

            leftVertices.remove(closest);

            indicesFrom(closest).forEach(i -> {
                double newDist = dist[closest] + adjacencyMatrix.get(closest, i) + vertexWeight.applyAsDouble(storedVertices.get(i));
                if (newDist < dist[i]) {
                    dist[i] = newDist;
                    prev[i] = closest;
                }
            });
        }

        return Collections.emptyList();
    }
}
