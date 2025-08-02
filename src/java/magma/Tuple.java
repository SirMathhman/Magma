package magma;

/**
 * A generic tuple class that holds two values of potentially different types.
 * 
 * This class is implemented as a Java record, providing immutable storage for a pair of values.
 * The values can be accessed using the automatically generated accessor methods {@code left()} and {@code right()}.
 * 
 * Example usage:
 * <pre>
 * Tuple&lt;String, Integer&gt; tuple = new Tuple&lt;&gt;("Hello", 42);
 * String leftValue = tuple.left();  // "Hello"
 * Integer rightValue = tuple.right();  // 42
 * </pre>
 *
 * @param <A> The type of the left value
 * @param <B> The type of the right value
 */
public record Tuple<A, B>(A left, B right) {}
