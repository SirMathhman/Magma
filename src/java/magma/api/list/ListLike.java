package magma.api.list;

public interface ListLike<T> {
    ListLike<T> add(T element);

    int size();

    T get(int index);
}
