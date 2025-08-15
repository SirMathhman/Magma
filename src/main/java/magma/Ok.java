package magma;

sealed public class Ok<T, E> implements Result<T, E> {
	private final T value;

	public Ok(T value) {
		this.value = value;
	}

	@Override
	public boolean isErr() {
		return false;
	}

	@Override
	public T unwrap() {
		return value;
	}

	@Override
	public E unwrapErr() {
		throw new RuntimeException("Called unwrapErr on Ok value");
	}
}
