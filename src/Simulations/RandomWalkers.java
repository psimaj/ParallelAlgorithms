package Simulations;

import Structures.SimpleGraph;
import Structures.Vertex;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class RandomWalkers {
    private SimpleGraph g;
    private Map<Vertex, Walker> takenBy = Collections.synchronizedMap(new HashMap<>());
    private final CountDownLatch starter;
    private final CountDownLatch ender;
    private Set<Thread> threads = new HashSet<>();

    public RandomWalkers(SimpleGraph g) {
        this.g = g;
        int walkersCount = g.getVertexCount()/2;
        ender = new CountDownLatch(walkersCount - 1);
        starter = new CountDownLatch(walkersCount);
        for (int i = 0; i < walkersCount; i++) {
            Thread t = new Thread(new Walker(g.getVertex(i), i));
            t.start();
            threads.add(t);
        }
    }

    public long run() {
        long startTime = 0, endTime = 0;
        try {
            starter.await();
            startTime = System.currentTimeMillis();
            ender.await();
            endTime = System.currentTimeMillis();
            for (Thread t : threads) {
                t.interrupt();
                t.join();
            }
        } catch (InterruptedException e) {}
        return endTime - startTime;
    }

    class Walker implements Runnable{
        Random generator = new Random();
        Vertex currentVertex;
        Thread currentThread;
        int timeAlive = 0;
        int index;

        public Walker(Vertex v, int index) {
            this.index = index;
            currentVertex = v;
            takenBy.put(currentVertex, this);
        }

        public void run() {
            try {
                currentThread = Thread.currentThread();
                starter.countDown();
                starter.await();
                while (!Thread.currentThread().isInterrupted()) {
                    //System.out.println(Thread.currentThread().getId() + " is in " + currentVertex.getID());
                    timeAlive++;
                    boolean shouldMove = generator.nextBoolean();
                    if (shouldMove) {
                        int chosenElement = generator.nextInt(g.getNeighbours(currentVertex.getID()).size());
                        Vertex moveTo = g.getNeighbours(currentVertex.getID()).get(chosenElement);
                        synchronized (currentVertex) {
                            synchronized (g.getNeighbours(currentVertex.getID()).get(chosenElement)) {
                                if (takenBy.keySet().contains(moveTo)) {
                                    System.out.println(index + " killed " + takenBy.get(moveTo).index);
                                    takenBy.get(moveTo).currentThread.interrupt();
                                }
                                takenBy.put(moveTo, this);
                                takenBy.keySet().remove(currentVertex);
                                currentVertex = moveTo;
                            }
                        }
                    }
                    Thread.sleep(10);
                }
            } catch (InterruptedException expected) {

            } finally {
                ender.countDown();
                printDeadMessage();
            }
        }

        private void printDeadMessage() {
            System.out.println(index +
                    " has been killed after " +
                    timeAlive +
                    " units of time.");
        }
    }
}
