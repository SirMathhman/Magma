package magma.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public interface ListLike<T> {
    @SafeVarargs
    static <T> ListLike<T> of(final T... elements) {
        return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
    }

    boolean contains(T element);

    Stream<T> stream();
}
