package magma;

public sealed interface Option<T> {
	record Some<T>(T value) implements Option<T> {}

	final class None<T> implements Option<T> {}
}
