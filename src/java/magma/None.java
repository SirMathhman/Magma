package magma;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

class None<T> implements Option<T> {

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
