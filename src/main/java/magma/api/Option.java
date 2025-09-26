package magma.api;

/**
 * Lightweight Option type: either Ok(value) or Err (absent).
 *
 * @param <T> contained type
 */
public sealed interface Option<T> permits Option.Some, Option.None {
	record Some<T>(T value) implements Option<T> {}

	record None<T>() implements Option<T> {}

	static <T> Option<T> of(T value) {
		return new Some<>(value);
	}

	static <T> Option<T> empty() {
		return new None<>();
	}
}
