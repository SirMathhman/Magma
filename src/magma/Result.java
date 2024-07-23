package magma;

public interface Result<T, E extends Exception> {
    T unwrap() throws E;
}
