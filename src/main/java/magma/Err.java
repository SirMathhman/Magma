package magma;

public record Err<T, E>(E error) implements Result<T, E> {
}
