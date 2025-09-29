package magma.compiler;

public sealed interface Result<T, E> permits Ok, Err {
	boolean isOk();

	T unwrap();

	E getError();

	static <T, E> Result<T, E> ok(T value) {
		return new Ok<>(value);
	}

	static <T, E> Result<T, E> err(E error) {
		return new Err<>(error);
	}
}
