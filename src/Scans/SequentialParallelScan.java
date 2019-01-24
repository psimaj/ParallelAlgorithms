package Scans;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class SequentialParallelScan {
    public static <T> List<T> compute(List<? extends T> elements, BinaryOperator<T> f){
        final int n = elements.size();
        final int log_n = getLog(n);

        final List<T> result = new ArrayList<>(elements);
        final List<T> tmp = new ArrayList<>(elements);

        int jumpBack = 1;
        for (int it = 0; it < log_n; it++) {
            for (int i = 0; i < n; i++) {
                tmp.set(i, result.get(i));
            }
            for (int i = jumpBack; i < n; i++) {
                result.set(i, f.apply(tmp.get(i - jumpBack), tmp.get(i)));
            }
            jumpBack *= 2;
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
