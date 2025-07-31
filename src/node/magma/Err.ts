package magma;

/**
 * An Err variant of Result representing a failed operation.
 *
 * @param <T> The type of the value (unused in this variant)
 * @param <E> The type of the error
 */
export class Err<T, E> implements Result<T, E> {
	private final E error;

	Err(final E error) {
		this.error = error;
	}

	@Override
	public boolean isErr(() {
		return true;
	}

	@Override
	public T getValue(() {
		throw new IllegalStateException("Cannot get value from Err result");
	}

	@Override
	public E getError(() {
		return this.error;
	}
}