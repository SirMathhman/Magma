package magma.option;

import magma.Tuple;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An implementation of {@link Option} that represents the absence of a value.
 * 
 * This class provides implementations of all Option methods that handle the case where no value is present.
 * Unlike {@link Some}, it doesn't need to store any value, so it's implemented as a regular class rather than a record.
 * 
 * Example usage:
 * <pre>
 * Option&lt;String&gt; name = new None&lt;&gt;();
 * String greeting = name.map(n -> "Hello, " + n).orElse("Hello, guest");  // "Hello, guest"
 * </pre>
 *
 * @param <T> The type of the value that would be present if this weren't None
 */
public class None<T> implements Option<T> {

	@Override
	public final <R> Option<R> map(final Function<T, R> mapper) {
		return new None<>();
	}

	@Override
	public final T orElse(final T other) {
		return other;
	}

	@Override
	public void ifPresent(final Consumer<T> consumer) {
	}

	@Override
	public final boolean isEmpty() {
		return true;
	}

	@Override
	public final Option<T> or(final Supplier<Option<T>> other) {
		return other.get();
	}

	@Override
	public final T orElseGet(final Supplier<T> other) {
		return other.get();
	}

	@Override
	public final <R> Option<R> flatMap(final Function<T, Option<R>> mapper) {
		return new None<>();
	}

	@Override
	public final Stream<T> stream() {
		return Stream.empty();
	}

	@Override
	public final Tuple<Boolean, T> toTuple(final T other) {
		return new Tuple<>(false, other);
	}
}
