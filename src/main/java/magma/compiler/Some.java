package magma.compiler;

public final class Some<T> implements Option<T> {
	private final T value;

	public Some(T value) {
		this.value = value;
	}

	@Override
	public java.util.Optional<T> asOptional() {
		return java.util.Optional.ofNullable(value);
	}
}
