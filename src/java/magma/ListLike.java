package magma;

public interface ListLike<T> {
    StreamLike<T> stream();

    ListLike<T> add(T element);
}
