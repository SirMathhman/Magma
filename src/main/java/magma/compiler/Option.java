package magma.compiler;

import java.util.Optional;

public sealed interface Option<T> permits Some, None {
	/**
	 * Access the value as a java.util.Optional. Prefer pattern matching on
	 * the sealed types instead of calling this when possible.
	 */
	Optional<T> asOptional();

	static <T> Option<T> some(T value) {
		return new Some<>(value);
	}

	static <T> Option<T> none() {
		return new None<>();
	}
}
