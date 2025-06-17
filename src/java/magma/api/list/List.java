package magma.api.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public interface List<T> {
    static <T> List<T> empty() {
        return new JavaList<>();
    }

    static <T> List<T> of(T... elements) {
        return new JavaList<>(new ArrayList<>(Arrays.asList(elements)));
    }

    Stream<T> stream();

    List<T> add(T element);

    java.util.List<T> unwrap();
}
