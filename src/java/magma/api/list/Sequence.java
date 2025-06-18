package magma.api.list;

public interface Sequence<T> extends Foldable<T> {
    int size();

    T get(int index);
}
