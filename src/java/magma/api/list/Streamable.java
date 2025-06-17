package magma.api.list;

public interface Streamable<T> {
    Iter<T> stream();
}
