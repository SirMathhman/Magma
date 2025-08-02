package magma.result;

import java.util.function.Function;

/**
 * An implementation of {@link Result} that represents a failed operation.
 * 
 * This class is implemented as a Java record with a single field 'error' of generic type X.
 * It represents the failure case in the Result pattern, containing the error information.
 * 
 * Example usage:
 * <pre>
 * Result&lt;Integer, String&gt; result = new Err&lt;&gt;("Division by zero");
 * String message = result.match(
 *     value -> "Success: " + value,
 *     error -> "Error: " + error
 * );  // "Error: Division by zero"
 * </pre>
 *
 * @param <T> The type of the successful result value (unused in this implementation)
 * @param <X> The type of the error
 * @param error The error information
 */
public record Err<T, X>(X error) implements Result<T, X> {
	/**
	 * Pattern matches on this Result, applying the whenErr function to the error.
	 * 
	 * Since this is an Err instance, the whenOk function is ignored.
	 *
	 * @param <R> The type of the result
	 * @param whenOk The function to apply if this were an Ok (ignored)
	 * @param whenErr The function to apply to the error
	 * @return The result of applying the whenErr function to the error
	 */
	@Override
	public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
		return whenErr.apply(this.error);
	}
}
