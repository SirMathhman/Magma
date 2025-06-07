package magma.list;

/** Minimal list abstraction mirroring java.util.List. */
public interface ListLike<T> extends Iterable<T> {
    void add(T value);
    T get(int index);
    void set(int index, T value);
    int size();
}
