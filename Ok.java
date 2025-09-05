public final class Ok<T, E> implements Result<T, E> {
	private final T value;

	public Ok(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}
}
