package magma.api;

public interface Action<T> {
    T perform() throws UnsafeException;
}
