package magma.result;

import java.util.function.Function;

/**
 * A container type that represents the result of an operation that might fail.
 * 
 * This is a functional programming pattern similar to Either in Haskell or Result in Rust.
 * It provides a way to handle errors without using exceptions, making error handling more
 * explicit and composable.
 * 
 * The Result type has two implementations:
 * <ul>
 *   <li>{@link Ok} - Represents a successful result containing a value</li>
 *   <li>{@link Err} - Represents a failure containing an error</li>
 * </ul>
 * 
 * Example usage:
 * <pre>
 * Result&lt;Integer, String&gt; divide(int a, int b) {
 *     if (b == 0) {
 *         return new Err&lt;&gt;("Division by zero");
 *     }
 *     return new Ok&lt;&gt;(a / b);
 * }
 * 
 * String result = divide(10, 2).match(
 *     value -> "Result: " + value,
 *     error -> "Error: " + error
 * );  // "Result: 5"
 * </pre>
 *
 * @param <T> The type of the successful result value
 * @param <X> The type of the error
 */
public interface Result<T, X> {
	/**
	 * Pattern matches on this Result, applying the appropriate function based on whether
	 * this is an Ok or an Err.
	 * 
	 * If this Result is Ok, the whenOk function is applied to the value.
	 * If this Result is Err, the whenErr function is applied to the error.
	 * 
	 * This method allows for handling both success and failure cases in a single expression.
	 *
	 * @param <R> The type of the result of both functions
	 * @param whenOk The function to apply if this Result is Ok
	 * @param whenErr The function to apply if this Result is Err
	 * @return The result of applying the appropriate function
	 */
	<R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
}
