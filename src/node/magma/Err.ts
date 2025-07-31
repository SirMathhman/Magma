package magma;

/**
 * An Err variant of Result representing a failed operation.
 *
 * @param <T> The type of the value (unused in this variant)
 * @param <E> The type of the error
 */
export class Err<T, E> implements Result<T, E> {
	private final E error;

	Err(E error) {
		this.error = error;
	}

	@Override
isErr() {
		return true;
	}

	@Override
getValue() {
		throw new IllegalStateException("Cannot get value from Err result");
	}

	@Override
getError() {
		return error;
	}
}