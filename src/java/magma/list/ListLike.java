package magma.list;

import magma.StreamLike;

public interface ListLike<T> {
    StreamLike<T> stream();

    ListLike<T> add(T element);
}
