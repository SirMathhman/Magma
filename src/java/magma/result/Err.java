package magma.result;

/**
 * An Err variant of Result representing a failed operation.
 *
 * @param <T> The type of the value (unused in this variant)
 * @param <E> The type of the error
 */
public record Err<T, E>(E error) implements Result<T, E> {
	@Override
	public boolean isErr() {
		return true;
	}

	@Override
	public T value() {
		throw new IllegalStateException("Cannot get value from Err result");
	}
}
