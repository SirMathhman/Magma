package magma.list;

import magma.option.None;
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
		return new HeadedStream<>(new Head<R>() {
			private Head<R> currentInnerHead = null;

			@Override
			public Option<R> next() {
				while (true) {
					// If we have a current inner stream, try to get the next element from it
					if (currentInnerHead != null) {
						final Option<R> innerNext = currentInnerHead.next();
						if (innerNext instanceof Some<R>(R value)) return innerNext;
						// Current inner stream is exhausted, move to next outer element
						currentInnerHead = null;
					}

					// Get the next element from the outer stream
					final Option<T> outerNext = head.next();
					if (outerNext instanceof Some<T>(T value)) {
						// Map to inner stream and set it as current
						final Stream<R> innerStream = mapper.apply(value);
						if (innerStream instanceof HeadedStream<R> headedStream) {
							currentInnerHead = headedStream.head();
						} else {
							throw new IllegalStateException("flatMap expects HeadedStream");
						}
						// Continue loop to get first element from this inner stream
					} else {
						// Outer stream is exhausted
						return new None<>();
					}
				}
			}
		});
	}

	@Override
	public boolean allMatch(Predicate<T> predicate) {
		return fold(true, (aBoolean, t) -> aBoolean && predicate.test(t));
	}
}
