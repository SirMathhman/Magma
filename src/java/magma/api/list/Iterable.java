package magma.api.list;

public interface Iterable<T> {
    Iter<T> stream();
}
