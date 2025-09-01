public sealed interface Result<T, X> permits Ok, Err {
}

public record Ok<T, X>(T value) implements Result<T, X> {
}

public record Err<T, X>(X error) implements Result<T, X> {
}
