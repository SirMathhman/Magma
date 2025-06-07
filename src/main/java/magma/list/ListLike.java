package magma.list;

/** Minimal list abstraction mirroring java.util.List but without Iterable. */
public interface ListLike<T> {
    void add(T value);
    T get(int index);
    void set(int index, T value);
    int size();
    ListIter<T> iterator();
}
