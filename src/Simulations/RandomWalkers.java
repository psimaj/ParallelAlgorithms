package Simulations;

import Structures.SimpleGraph;
import Structures.Vertex;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class RandomWalkers {
    private SimpleGraph g;
    private Map<Vertex, Walker> takenBy = Collections.synchronizedMap(new HashMap<>());
    private final CountDownLatch starter;
    private final CountDownLatch ender;
    private Set<Thread> threads = new HashSet<>();
    private Map<Vertex, Lock> vertexLocks = new HashMap<>();

    public RandomWalkers(SimpleGraph g) {
        this.g = g;
        int walkersCount = g.getVertexCount() / 2;
        for (Vertex v : g.getVertices()) {
            vertexLocks.put(v, new ReentrantLock());
        }
        ender = new CountDownLatch(walkersCount - 1);
        starter = new CountDownLatch(walkersCount);
        for (int i = 0; i < walkersCount; i++) {
            Thread t = new Thread(new Walker(g.getVertex(i), i));
            t.start();
            threads.add(t);
        }

        //register deadlock detection
        ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);
        ex.scheduleAtFixedRate(() -> {
            Set<Long> deadlocks = Arrays.stream(ManagementFactory.getThreadMXBean().findDeadlockedThreads()).boxed().collect(Collectors.toSet());
            for (Thread t : threads) {
                if (deadlocks.contains(t.getId())) {
                    System.out.println("Killing deadlocked thread " + t.getId());
                    t.interrupt();
                }
            }
        }, 0, (10000 + g.getEdgeCount()) / g.getEdgeCount(), TimeUnit.MILLISECONDS);
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
        } catch (InterruptedException e) {
        }
        return endTime - startTime;
    }


    class Walker implements Runnable {
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
                    timeAlive++;
                    boolean shouldMove = generator.nextBoolean();
                    if (shouldMove) {
                        int chosenElement = generator.nextInt(g.getNeighbours(currentVertex.getID()).size());
                        Vertex to = g.getNeighbours(currentVertex.getID()).get(chosenElement);
                        Vertex from = currentVertex;
                        try {
                            vertexLocks.get(from).lockInterruptibly();
                            try {
                                vertexLocks.get(to).lockInterruptibly();
                                if (takenBy.keySet().contains(to)) {
                                    System.out.println(index + " killed " + takenBy.get(to).index);
                                    takenBy.get(to).currentThread.interrupt();
                                }
                                takenBy.put(to, this);
                                takenBy.keySet().remove(currentVertex);
                                currentVertex = to;
                                vertexLocks.get(to).unlock();
                            } catch (InterruptedException e) {
                                vertexLocks.get(from).unlock();
                                return;
                            }
                            vertexLocks.get(from).unlock();
                        } catch (InterruptedException e) {
                            return;
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
