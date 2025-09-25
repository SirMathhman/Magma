package magma;

/**
 * Lightweight Option type: either Ok(value) or Err (absent).
 *
 * @param <T> contained type
 */
public sealed interface Option<T> permits Option.Ok, Option.Err {

	record Ok<T>(T value) implements Option<T> {
	}

	record Err<T>() implements Option<T> {
	}

	static <T> Option<T> ok(T value) {
		return new Ok<>(value);
	}

	static <T> Option<T> err() {
		return new Err<>();
	}

}
