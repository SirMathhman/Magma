package magma;

import java.util.stream.Stream;

public interface ListLike<T> extends Iterable<T> {
    ListLike<T> add(T element);

    Stream<T> stream();
}
