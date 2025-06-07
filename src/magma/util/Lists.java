package magma.util;

import java.util.ArrayList;
import java.util.Arrays;

public final class Lists {
    public static <T> List<T> empty() {
        return new JavaList<>();
    }

    @SafeVarargs
    public static <T> List<T> of(T... elements) {
        return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
    }
}
