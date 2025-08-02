package magma.result;

import java.util.function.Function;

/**
 * An implementation of {@link Result} that represents a successful operation.
 * 
 * This class is implemented as a Java record with a single field 'value' of generic type T.
 * It represents the success case in the Result pattern, containing the successful result value.
 * 
 * Example usage:
 * <pre>
 * Result&lt;Integer, String&gt; result = new Ok&lt;&gt;(42);
 * String message = result.match(
 *     value -> "Success: " + value,
 *     error -> "Error: " + error
 * );  // "Success: 42"
 * </pre>
 *
 * @param <T> The type of the successful result value
 * @param <X> The type of the error (unused in this implementation)
 * @param value The successful result value
 */
public record Ok<T, X>(T value) implements Result<T, X> {
	/**
	 * Pattern matches on this Result, applying the whenOk function to the value.
	 * 
	 * Since this is an Ok instance, the whenErr function is ignored.
	 *
	 * @param <R> The type of the result
	 * @param whenOk The function to apply to the value
	 * @param whenErr The function to apply if this were an Err (ignored)
	 * @return The result of applying the whenOk function to the value
	 */
	@Override
	public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
		return whenOk.apply(this.value);
	}
}
