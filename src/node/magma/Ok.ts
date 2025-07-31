package magma;

/**
 * An Ok variant of Result representing a successful operation.
 *
 * @param <T> The type of the value
 * @param <E> The type of the error (unused in this variant)
 */
export class Ok<T, E> implements Result<T, E> {
	private final T value;

	Ok(final T value) {
		this.value = value;
	}

	@Override
	public boolean isErr(() {
		return false;
	}

	@Override
	public T getValue(() {
		return this.value;
	}

	@Override
	public E getError(() {
		throw new IllegalStateException("Cannot get error from Ok result");
	}
}