package magma.api.list;

import java.util.stream.Stream;

public interface List<T> {
    Stream<T> stream();

    List<T> add(T element);
}
