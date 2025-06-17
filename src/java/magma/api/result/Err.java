package magma.api.result;

public record Err<T, X>(X error) implements Result<T, X> {
}
