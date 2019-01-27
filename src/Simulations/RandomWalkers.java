package Simulations;

import Structures.SimpleGraph;
import Structures.Vertex;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class RandomWalkers {
    private SimpleGraph g;
    private Map<Vertex, Walker> takenBy = Collections.synchronizedMap(new HashMap<>());
    private final CountDownLatch starter;
    private final CountDownLatch ender;
    private Set<Thread> threads = new HashSet<>();
    private Map<Vertex, ReentrantLock> vertexLocks = new HashMap<>();

    public RandomWalkers(SimpleGraph g, Collection<Vertex> walkersStartingPoints) {
        this.g = g;
        for (Vertex v : g.getVertices()) {
            vertexLocks.put(v, new ReentrantLock());
        }
        ender = new CountDownLatch(walkersStartingPoints.size() - 1);
        starter = new CountDownLatch(walkersStartingPoints.size() + 1);
        int i = 0;
        for (Vertex v : walkersStartingPoints) {
            Thread t = new Thread(new Walker(v, i++));
            t.start();
            threads.add(t);
        }

        //register deadlock detection
        ScheduledExecutorService ex = Executors.newScheduledThreadPool(1);
        ex.scheduleAtFixedRate(() -> {
            long[] deadlocksArr = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
            if (deadlocksArr == null) {
                return;
            }
            Set<Long> deadlocks = Arrays.stream(deadlocksArr).boxed().collect(Collectors.toSet());
            for (Thread t : threads) {
                if (deadlocks.contains(t.getId())) {
                    System.out.println("Killing deadlocked thread " + t.getId());
                    t.interrupt();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public long run() {
        if (!g.isConnected()) {
            System.out.println("The graph is not connected");
            return -1;
        }
        long startTime = 0, endTime = 0;
        try {
            starter.countDown();
            starter.await();
            startTime = System.currentTimeMillis();
            ender.await();
            endTime = System.currentTimeMillis();
            for (Thread t : threads) {
                t.interrupt();
                t.join();
            }
        } catch (InterruptedException e) {
        }
        return endTime - startTime;
    }


    class Walker implements Runnable {
        private Random generator = new Random();
        private Vertex currentVertex;
        private Thread currentThread;
        private int timeAlive = 0;
        private int index;

        Walker(Vertex v, int index) {
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
                    timeAlive++;
                    int chosenElement = generator.nextInt(g.getNeighbours(currentVertex.getID()).size());
                    Vertex to = g.getNeighbours(currentVertex.getID()).get(chosenElement);
                    Vertex from = currentVertex;
                    try {
                        vertexLocks.get(from).lockInterruptibly();
                        try {
                            vertexLocks.get(to).lockInterruptibly();
                            if (takenBy.keySet().contains(to)) {
                                takenBy.get(to).interrupt();
                            }
                            takenBy.put(to, this);
                            takenBy.keySet().remove(currentVertex);
                            currentVertex = to;
                        } catch (InterruptedException e) {
                            return;
                        } finally {
                            if (vertexLocks.get(to).isHeldByCurrentThread()) {
                                vertexLocks.get(to).unlock();
                            }
                        }
                    } catch (InterruptedException e) {
                        return;
                    } finally {
                        if (vertexLocks.get(from).isHeldByCurrentThread()) {
                            vertexLocks.get(from).unlock();
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

        void interrupt() {
            currentThread.interrupt();
        }

        private void printDeadMessage() {
            System.out.println(index +
                    " has been killed after " +
                    timeAlive +
                    " units of time.");
        }
    }
}
