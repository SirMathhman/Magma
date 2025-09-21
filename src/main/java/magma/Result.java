package magma;

public sealed interface Result<T, E> {
	record Ok<T, E>(T value) implements Result<T, E> {
	}

	record Err<T, E>(E error) implements Result<T, E> {
	}
}
