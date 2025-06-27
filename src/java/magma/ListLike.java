package magma;

import java.util.stream.Stream;

public interface ListLike<T> {
    Stream<T> stream();

    ListLike<T> add(T element);

    Optional<Tuple<ListLike<T>, T>> popLast();

    Optional<Tuple<T, ListLike<T>>> popFirst();

    boolean isEmpty();
}
