package magma.list;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public record HeadedStream<T>(Head<T> head) implements Stream<T> {
	private class FlatMapHead<R> implements Head<R> {
		private final Function<T, Stream<R>> mapper;
		private Option<Head<R>> currentInnerHead;

		public FlatMapHead(Function<T, Stream<R>> mapper) {
			this.mapper = mapper;
			currentInnerHead = new None<Head<R>>();
		}

		@Override
		public Option<R> next() {
			while (true) {
				// If we have a current inner stream, try to get the next element from it
				if (currentInnerHead instanceof Some<Head<R>>(Head<R> innerHead)) {
					final Option<R> innerNext = innerHead.next();
					if (innerNext instanceof Some<R>(R value))
						return innerNext;
					// Current inner stream is exhausted, move to next outer element
					currentInnerHead = new None<Head<R>>();
				}

				// Get the next element from the outer stream
				final Option<T> outerNext = head.next();
				// Outer stream is exhausted
				if (outerNext instanceof Some<T>(T value)) {
					// Map to inner stream and set it as current
					final Stream<R> innerStream = mapper.apply(value);
					// Return error instead of throwing
					if (innerStream instanceof HeadedStream<R>(Head<R> head1)) currentInnerHead = new Some<Head<R>>(head1);
					else return new None<R>();
					// Continue loop to get first element from this inner stream
				} else
					return new None<R>();
			}
		}
	}

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
			if (map instanceof Some<R>(R value))
				current = value;
			else
				return current;
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
			if (next instanceof Some<T>(T temp))
				consumer.accept(temp);
			else
				break;
		}
	}

	@Override
	public <R> Stream<R> flatMap(Function<T, Stream<R>> mapper) {
		return new HeadedStream<R>(new FlatMapHead<R>(mapper));
	}

	@Override
	public Stream<T> filter(Predicate<T> predicate) {
		return new HeadedStream<T>(() -> {
			while (true) {
				final Option<T> next = head.next();
				if (next instanceof Some<T>(T value)) {
					if (predicate.test(value))
						return next;
				} else
					return new None<T>();
			}
		});
	}

	@Override
	public boolean allMatch(Predicate<T> predicate) {
		return fold(true, (aBoolean, t) -> aBoolean && predicate.test(t));
	}

	@Override
	public boolean anyMatch(Predicate<T> predicate) {
		return fold(false, (aBoolean, t) -> aBoolean || predicate.test(t));
	}
}
