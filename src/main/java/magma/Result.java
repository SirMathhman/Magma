package magma;

public sealed interface Result<T, E> permits Err, Ok {
	boolean isErr();

	T unwrap();

	E unwrapErr();
}