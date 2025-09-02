package magma.util;

public record Ok<T, X>(T value) implements Result<T, X> {}
