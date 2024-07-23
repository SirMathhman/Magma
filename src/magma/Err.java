package magma;

public record Err<T, E extends Exception>(E e) implements Result<T, E> {
    @Override
    public T unwrap() throws E {
        throw e;
    }
}
