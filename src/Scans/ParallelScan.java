package Scans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.BinaryOperator;

public class ParallelScan {
    private static final int maxThreadCount = 1000;

    public static <T> List<T> compute(List<? extends T> elements, BinaryOperator<T> f){
        final int n = elements.size();
        final int log_n = getLog(n);
        final int threadCount = Math.min(n, maxThreadCount);

        final List<T> result = new ArrayList<>(elements);
        final List<T> tmp = new ArrayList<>(elements);
        final CountDownLatch[] firstStageLocks = new CountDownLatch[log_n];
        final CountDownLatch[] secondStageLocks = new CountDownLatch[log_n];
        for(int i = 0; i < firstStageLocks.length; i++) {
            firstStageLocks[i] = new CountDownLatch(threadCount);
            secondStageLocks[i] = new CountDownLatch(threadCount);
        }
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int j = i;
            Thread t = new Thread(() -> {
                int jumpBack = 1;
                for (int it = 0; it < log_n; it++) {
                    for (int k = j; k < n; k += threadCount) {
                        tmp.set(k, result.get(k));

                    }
                    firstStageLocks[it].countDown();
                    boolean awoken = false;
                    while (!awoken) {
                        try {
                            firstStageLocks[it].await();
                            awoken = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int k = j; k < n; k += threadCount) {
                        if (k - jumpBack >= 0) {
                            result.set(k, f.apply(tmp.get(k - jumpBack), tmp.get(k)));
                        }
                    }
                    jumpBack *= 2;
                    secondStageLocks[it].countDown();
                    awoken = false;
                    while (!awoken) {
                        try {
                            secondStageLocks[it].await();
                            awoken = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.start();
            threads.add(t);
        }
        for (int i = 0; i < threadCount; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                i--;
            }
        }
        return result;
    }

    private static int getLog(int n) {
        int j = 0;
        for (int i = 1; i < n; i *= 2) {
            j++;
        }
        return j;
    }
}
