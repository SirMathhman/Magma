package magma.api;

public record Err<T, X>(X error) implements Result<T, X> {
}
