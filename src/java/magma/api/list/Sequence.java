package magma.api.list;

public interface Sequence<T> {
    int size();

    T get(int index);
}
