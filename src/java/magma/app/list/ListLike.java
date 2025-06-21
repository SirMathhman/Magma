package magma.app.list;

import magma.app.StreamLike;

public interface ListLike<T> {
    StreamLike<T> stream();

    ListLike<T> add(T element);
}
