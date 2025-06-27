package magma;

import java.util.stream.Stream;

public interface ListLike<T> {
    Stream<T> stream();

    ListLike<T> add(T element);
}
