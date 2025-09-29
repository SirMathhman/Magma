package magma.compiler;

public final class Err<T, E> implements Result<T, E> {
	private final E error;

	public Err(E error) {
		this.error = error;
	}

	@Override
	public boolean isOk() {
		return false;
	}

	@Override
	public T unwrap() {
		throw new java.util.NoSuchElementException("Err.unwrap()");
	}

	@Override
	public E getError() {
		return error;
	}
}
