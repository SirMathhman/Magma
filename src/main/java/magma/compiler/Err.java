package magma.compiler;

public final class Err<T, E> implements Result<T, E> {
	private final E error;

	public Err(E error) {
		this.error = error;
	}

	@Override
	public java.util.Optional<T> asOptional() {
		return java.util.Optional.empty();
	}

	@Override
	public java.util.Optional<E> asErrorOptional() {
		return java.util.Optional.ofNullable(error);
	}
}
