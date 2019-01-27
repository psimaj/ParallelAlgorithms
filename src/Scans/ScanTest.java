package Scans;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class ScanTest {

    public static <T> void runTest(List<T> num, BinaryOperator<T> f) {
        System.out.println();
        long startTime, endTime, totalTime;

        startTime = System.currentTimeMillis();

        List<T> sequentialResults = SequentialScan.compute(num, f);

        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("Sequential execution time: " + totalTime + "ms");

        startTime = System.currentTimeMillis();

        List<T> sequentialParallelResults = SequentialParallelScan.compute(num, f);

        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("Sequential parallel execution time: " + totalTime + "ms");

        startTime = System.currentTimeMillis();

        List<T> parallelResults = ParallelScan.compute(num, f);

        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("Parallel execution time: " + totalTime + "ms");

        for (int i = 0; i < num.size(); i++) {
            if (!sequentialResults.get(i).equals(parallelResults.get(i)) ||
                !sequentialResults.get(i).equals(sequentialParallelResults.get(i))) {
                System.out.println("Wrong answer at index: " + i);
                return;
            }
        }
        System.out.println();
    }

    @Test
    public static void test1() {
        List<Integer> num = new ArrayList<>();
        System.out.println("10 integers, + operation");
        for (int i = 0; i < 10; i++) {
            num.add(i);
        }
        runTest(num, (x, y) -> x + y);
    }

    @Test
    public static void test2() {
        List<Integer> num = new ArrayList<>();
        System.out.println("100 integers, * operation");
        for (int i = 0; i < 100; i++) {
            num.add(1);
        }
        runTest(num, (x, y) -> x * y);
    }

    @Test
    public static void test3() {
        List<Integer> num = new ArrayList<>();
        System.out.println("1000000 integers, + operation");
        for (int i = 0; i < 1000000; i++) {
            num.add(1);
        }
        runTest(num, (x, y) -> x + y);
    }

    @Test
    public static void test4() {
        List<Integer> num = new ArrayList<>();
        System.out.println("10000000 integers, * operation");
        for (int i = 0; i < 10000000; i++) {
            num.add(1);
        }
        runTest(num, (x, y) -> x * y);
    }
}
