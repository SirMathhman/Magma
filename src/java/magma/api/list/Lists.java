package magma.api.list;

import java.util.ArrayList;
import java.util.Arrays;

public class Lists {
    public static <T> List<T> empty() {
        return new JavaList<>();
    }

    public static <T> Iterable<T> of(T... elements) {
        return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
    }
}
