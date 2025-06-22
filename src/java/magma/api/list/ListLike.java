package magma.api.list;

import java.util.stream.Stream;

public interface ListLike<T> {
    boolean contains(T element);

    Stream<T> stream();

    ListLike<T> add(T element);
}
