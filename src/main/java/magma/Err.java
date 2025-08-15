package magma;

public final class Err<T, E> implements Result<T, E> {
	private final E error;

	public Err(E error) {
		this.error = error;
	}

	@Override
	public boolean isErr() {
		return true;
	}

	@Override
	public T unwrap() {
		throw new RuntimeException("Called unwrap on Err value");
	}

	@Override
	public E unwrapErr() {
		return error;
	}
}
