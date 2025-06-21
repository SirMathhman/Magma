package magma.api.collect.list;

import magma.api.collect.stream.StreamLike;

public interface ListLike<T> {
    StreamLike<T> stream();

    ListLike<T> add(T element);
}
