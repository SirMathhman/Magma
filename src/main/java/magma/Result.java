package magma;

/**
 * Simple generic Result type: either Ok with a value, or Err with an error
 * value.
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {
	record Ok<T, E>(T value) implements Result<T, E> {}

	record Err<T, E>(E error) implements Result<T, E> {}
}
