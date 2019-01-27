package Tests;

import Simulations.RandomWalkers;
import Structures.SimpleGraph;
import Structures.Vertex;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class WalkersTest {

    private long runTest(SimpleGraph g, Collection<Vertex> walkers) {
        return (new RandomWalkers(g, walkers)).run();
    }

    @Test
    public void testLine() {
        SimpleGraph line = new SimpleGraph();
        int vertices = 20;
        for (int i = 0; i < vertices; i++) {
            line.addVertex(i);
        }
        for (int i = 0; i < vertices - 1; i++) {
            line.addEdge(i, i + 1);
        }
        Collection<Vertex> walkers = getRandomVertices(line.getVertices(), vertices/2);
        printTime(runTest(line, walkers));
    }

    @Test
    public void testCycle() {
        SimpleGraph cycle = new SimpleGraph();
        int vertices = 20;
        for (int i = 0; i < vertices; i++) {
            cycle.addVertex(i);
        }
        for (int i = 0; i < vertices; i++) {
            cycle.addEdge(i, (i + 1) % vertices);
        }
        Collection<Vertex> walkers = getRandomVertices(cycle.getVertices(), vertices/2);
        printTime(runTest(cycle, walkers));
    }

    @Test
    public void testClique() {
        SimpleGraph clique = new SimpleGraph();
        int vertices = 20;
        for (int i = 0; i < vertices; i++) {
            clique.addVertex(i);
        }
        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                clique.addEdge(i, j);
            }
        }
        Collection<Vertex> walkers = getRandomVertices(clique.getVertices(), vertices/2);
        printTime(runTest(clique, walkers));
    }

    @Test
    public void testRandom() {
        SimpleGraph random = new SimpleGraph();
        int vertices = 20;
        for (int i = 0; i < vertices; i++) {
            random.addVertex(i);
        }
        List<Integer> v = IntStream.range(0, vertices).boxed().collect(Collectors.toList());
        Collections.shuffle(v);
        for (int i = 0; i < vertices - 1; i++) {
            random.addEdge(v.get(i), v.get(i+1));
        }
        int estimatedRandomEdges = 100;
        Random r = new Random();
        for (int i = 0; i < estimatedRandomEdges; i++) {
            random.addEdge(r.nextInt(vertices), r.nextInt(vertices));
        }
        Collection<Vertex> walkers = getRandomVertices(random.getVertices(), vertices/2);
        printTime(runTest(random, walkers));
    }

    private static void printTime(long t) {
        System.out.println("Process finished after " + t + " ms");
    }

    private Set<Vertex> getRandomVertices(Collection<Vertex> v, int n) {
        if (n >= v.size()) {
            n = v.size();
        }
        Vertex[] vArr = v.stream().toArray(Vertex[]::new);
        Collections.shuffle(Arrays.asList(vArr));
        Set<Vertex> result = new HashSet<>();
        for (int i = 0; i < n; i++) {
            result.add(vArr[i]);
        }
        return result;
    }
}
