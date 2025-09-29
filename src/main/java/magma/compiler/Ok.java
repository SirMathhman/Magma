package magma.compiler;

public final class Ok<T, E> implements Result<T, E> {
	private final T value;

	public Ok(T value) {
		this.value = value;
	}

	@Override
	public boolean isOk() {
		return true;
	}

	@Override
	public T unwrap() {
		return value;
	}

	@Override
	public E getError() {
		return null;
	}
}
