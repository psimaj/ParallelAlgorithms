package Simulations;

import Structures.SimpleGraph;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class WalkersTest {

    public long runTest(SimpleGraph g) {
        return (new RandomWalkers(g)).run();
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
        printTime(runTest(cycle));
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
        printTime(runTest(clique));
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
        printTime(runTest(random));
    }

    private static void printTime(long t) {
        System.out.println("Process finished after " + t + " ms");
    }
}
