package magma.result;

/**
 * An Ok variant of Result representing a successful operation.
 *
 * @param <T> The type of the value
 * @param <E> The type of the error (unused in this variant)
 */
public record Ok<T, E>((value: T) implements Result<T, E> {
	@Override
	public boolean isErr(() {
		return false;
	}

	@Override
	public E error(() {
		throw new IllegalStateException("Cannot get error from Ok result");
	}
}