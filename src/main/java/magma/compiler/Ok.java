package magma.compiler;

public final class Ok<T, E> implements Result<T, E> {
	private final T value;

	public Ok(T value) {
		this.value = value;
	}

	@Override
	public java.util.Optional<T> asOptional() {
		return java.util.Optional.ofNullable(value);
	}

	@Override
	public java.util.Optional<E> asErrorOptional() {
		return java.util.Optional.empty();
	}
}
