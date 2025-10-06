package magma.list;

import magma.option.Option;
import magma.option.Some;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public record HeadedStream<T>(Head<T> head) implements Stream<T> {
	@Override
	public <R> Stream<R> map(Function<T, R> mapper) {
		return new HeadedStream<R>(() -> head.next().map(mapper));
	}

	@Override
	public <R> R fold(R initial, BiFunction<R, T, R> folder) {
		R current = initial;
		while (true) {
			R finalCurrent = current;
			final Option<R> map = head.next().map(inner -> folder.apply(finalCurrent, inner));
			if (map instanceof Some<R>(R value)) current = value;
			else return current;
		}
	}

	@Override
	public <R> R collect(Collector<T, R> collector) {
		return fold(collector.initial(), collector::fold);
	}

	@Override
	public void forEach(Consumer<T> consumer) {
		while (true) {
			final Option<T> next = head.next();
			if (next instanceof Some<T>(T temp)) consumer.accept(temp);
			else break;
		}
	}

	@Override
	public <R> Stream<R> flatMap(Function<T, Stream<R>> mapper) {
		// TODO: implement this
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean allMatch(Predicate<T> predicate) {
		return fold(true, (aBoolean, t) -> aBoolean && predicate.test(t));
	}
}
