package Scans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;

public class SequentialScan {
    public static <T> List<T> compute(List<? extends T> elements, BinaryOperator<T> f){
        final List<T> result = Collections.synchronizedList(new ArrayList<>(elements));
        result.set(0, elements.get(0));
        for (int i = 1; i < elements.size(); i++) {
            result.set(i, f.apply(result.get(i-1), elements.get(i)));
        }
        return result;
    }
}
