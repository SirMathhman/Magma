package magma;

public record Ok<T, E extends Exception>(T value) implements Result<T, E> {
    @Override
    public T unwrap() {
        return value;
    }
}
