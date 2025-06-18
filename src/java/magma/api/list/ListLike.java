package magma.api.list;

public interface ListLike<T> extends Sequence<T> {
    ListLike<T> add(T element);

    ListLike<T> addAll(Sequence<T> others);
}
