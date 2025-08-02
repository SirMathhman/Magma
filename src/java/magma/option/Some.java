package magma.option;

import magma.Tuple;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An implementation of {@link Option} that represents the presence of a value.
 * 
 * This class is implemented as a Java record with a single field 'value' of generic type T.
 * It provides implementations of all Option methods that handle the case where a value is present.
 * 
 * Example usage:
 * <pre>
 * Option&lt;String&gt; name = new Some&lt;&gt;("John");
 * String greeting = name.map(n -> "Hello, " + n).orElse("Hello, guest");  // "Hello, John"
 * </pre>
 *
 * @param <T> The type of the value
 * @param value The value that is present
 */
public record Some<T>(T value) implements Option<T> {

	@Override
	public <R> Option<R> map(final Function<T, R> mapper) {
		return new Some<>(mapper.apply(this.value));
	}

	@Override
	public T orElse(final T other) {
		return this.value;
	}

	@Override
	public void ifPresent(final Consumer<T> consumer) {
		consumer.accept(this.value);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Option<T> or(final Supplier<Option<T>> other) {
		return this;
	}

	@Override
	public T orElseGet(final Supplier<T> other) {
		return this.value;
	}

	@Override
	public <R> Option<R> flatMap(final Function<T, Option<R>> mapper) {
		return mapper.apply(this.value);
	}

	@Override
	public Stream<T> stream() {
		return Stream.of(this.value);
	}

	@Override
	public Tuple<Boolean, T> toTuple(final T other) {
		return new Tuple<>(true, this.value);
	}
}
