package magma;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

interface Option<T> {
	<R> Option<R> map(Function<T, R> mapper);

	T orElse(T other);

	void ifPresent(Consumer<T> consumer);

	boolean isEmpty();

	Option<T> or(Supplier<Option<T>> other);

	T orElseGet(Supplier<T> other);

	<R> Option<R> flatMap(Function<T, Option<R>> mapper);

	Stream<T> stream();

	Tuple<Boolean, T> toTuple(T other);
}
