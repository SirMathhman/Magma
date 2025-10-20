package magma;

public class Results {
	public sealed interface Result<T, X> permits Err, Ok {}

	record Ok<T, X>(T value) implements Result<T, X> {}

	record Err<T, X>(X error) implements Result<T, X> {}
}
