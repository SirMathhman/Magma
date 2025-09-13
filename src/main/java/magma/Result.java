package magma;

public sealed interface Result<T, E> permits Ok, Err {
}

final record Ok<T, E>(T value) implements Result<T, E> {
}

final record Err<T, E>(E error) implements Result<T, E> {
}
