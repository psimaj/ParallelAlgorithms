import Simulations.RandomWalkers;
import Structures.SimpleGraph;

public class WalkersTest {
    public static long runTest(SimpleGraph g) {
        return (new RandomWalkers(g)).run();
    }

    public static void test1() {
        SimpleGraph g = new SimpleGraph();
        for (int i = 0; i < 20; i++) {
            g.addVertex(i);
        }
        for (int i = 0; i < 20; i++) {
            g.addEdge(i, (i + 1) % 20);
        }
        long elapsedTime = runTest(g);
        System.out.println("Process finished after " + elapsedTime + "ms");
    }
}
