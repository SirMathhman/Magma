package magma.compiler;

public sealed interface Option<T> permits Some, None {
	boolean isPresent();

	T get();

	static <T> Option<T> some(T value) {
		return new Some<>(value);
	}

	@SuppressWarnings("unchecked")
	static <T> Option<T> none() {
		return (Option<T>) None.instance();
	}
}
