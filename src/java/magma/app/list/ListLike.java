package magma.app.list;

import magma.app.stream.StreamLike;

public interface ListLike<T> {
    StreamLike<T> stream();

    ListLike<T> add(T element);
}
