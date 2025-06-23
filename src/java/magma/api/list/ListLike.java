package magma.api.list;

public interface ListLike<T> extends Streamable<T> {
    boolean contains(T element);

    ListLike<T> add(T element);
}
