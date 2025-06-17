package magma.api.collect.list;

import magma.api.collect.iter.Iterable;

import java.util.ArrayList;
import java.util.Arrays;

public class Lists {
    public static <T> List<T> empty() {
        return new JavaList<>();
    }

    @SafeVarargs
    public static <T> Iterable<T> of(T... elements) {
        return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
    }
}
