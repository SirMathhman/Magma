package magma.list;

import java.util.stream.Stream;

public interface ListLike<T> {
    boolean contains(T element);

    Stream<T> stream();
}
