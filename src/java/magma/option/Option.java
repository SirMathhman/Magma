package magma.option;

import magma.Tuple;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container type that represents an optional value: either Some value or None.
 * 
 * This is a functional programming pattern similar to Java's Optional class, but with
 * additional methods and a more functional approach. It provides a way to handle potentially
 * absent values without using null references.
 * 
 * The Option type has two implementations:
 * <ul>
 *   <li>{@link Some} - Represents the presence of a value</li>
 *   <li>{@link None} - Represents the absence of a value</li>
 * </ul>
 * 
 * Example usage:
 * <pre>
 * Option&lt;String&gt; maybeName = getUserName(userId);
 * String greeting = maybeName.map(name -> "Hello, " + name)
 *                           .orElse("Hello, guest");
 * </pre>
 *
 * @param <T> The type of the value that might be present
 */
public interface Option<T> {
	/**
	 * Transforms the value inside this Option if it is present.
	 * 
	 * If this Option is Some, applies the mapper function to the value and returns a new Option
	 * containing the result. If this Option is None, returns None.
	 *
	 * @param <R> The type of the result
	 * @param mapper The function to apply to the value
	 * @return A new Option containing the transformed value, or None
	 */
	<R> Option<R> map(Function<T, R> mapper);

	/**
	 * Returns the value if present, otherwise returns the specified default value.
	 *
	 * @param other The default value to return if this Option is None
	 * @return The value if present, otherwise the default value
	 */
	T orElse(T other);

	/**
	 * Executes the given consumer with the value if present.
	 * 
	 * If this Option is Some, the consumer is called with the value.
	 * If this Option is None, nothing happens.
	 *
	 * @param consumer The consumer to execute with the value
	 */
	void ifPresent(Consumer<T> consumer);

	/**
	 * Checks if this Option is empty (None).
	 *
	 * @return true if this Option is None, false if it is Some
	 */
	boolean isEmpty();

	/**
	 * Returns this Option if it is Some, otherwise returns the Option provided by the supplier.
	 *
	 * @param other A supplier that provides an alternative Option
	 * @return This Option if it is Some, otherwise the alternative Option
	 */
	Option<T> or(Supplier<Option<T>> other);

	/**
	 * Returns the value if present, otherwise returns the value provided by the supplier.
	 *
	 * @param other A supplier that provides a default value
	 * @return The value if present, otherwise the value from the supplier
	 */
	T orElseGet(Supplier<T> other);

	/**
	 * Transforms the value inside this Option using a function that returns another Option.
	 * 
	 * If this Option is Some, applies the mapper function to the value and returns the result.
	 * If this Option is None, returns None.
	 * 
	 * This is similar to map, but the mapper function returns an Option instead of a plain value.
	 *
	 * @param <R> The type of the result
	 * @param mapper The function to apply to the value
	 * @return The Option returned by the mapper function, or None
	 */
	<R> Option<R> flatMap(Function<T, Option<R>> mapper);

	/**
	 * Converts this Option to a Stream.
	 * 
	 * If this Option is Some, returns a Stream containing the value.
	 * If this Option is None, returns an empty Stream.
	 *
	 * @return A Stream containing the value if present, otherwise an empty Stream
	 */
	Stream<T> stream();

	/**
	 * Converts this Option to a Tuple containing a boolean indicating presence and the value.
	 * 
	 * If this Option is Some, returns a Tuple with true and the value.
	 * If this Option is None, returns a Tuple with false and the provided default value.
	 *
	 * @param other The default value to use if this Option is None
	 * @return A Tuple containing a boolean indicating presence and the value
	 */
	Tuple<Boolean, T> toTuple(T other);
}
