package magma;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Streams {
	public record Stream<T>(Heads.Head<T> head) {
		<R> Stream<R> map(Function<T, R> mapper) {
			return new Stream<R>(() -> this.head.next().map(mapper));
		}

		public Stream<T> filter(Predicate<T> predicate) {
			final Heads.Head<T> sourceHead = this.head;
			return new Stream<T>(() -> {
				while (true) {
					Options.Option<T> nextValue = sourceHead.next();
					if (nextValue instanceof Options.Some<T>(T value)) {
						if (predicate.test(value)) {
							return new Options.Some<T>(value);
						}
						// Continue to next element
					} else {
						return new Options.None<T>();
					}
				}
			});
		}

		public <C> C collect(Collectors.Collector<T, C> collector) {
			return this.foldWithInitial(collector.createInitial(), collector::fold);
		}

		public <C> C foldWithInitial(C initial, BiFunction<C, T, C> folder) {
			C accumulator = initial;
			Options.Option<T> current = this.head.next();
			while (current instanceof Options.Some<T>(T value)) {
				accumulator = folder.apply(accumulator, value);
				current = this.head.next();
			}
			return accumulator;
		}

		public <R> Stream<R> flatMap(Function<T, Stream<R>> mapper) {
			return new Stream<R>(new Heads.FlatMapHead<T, R>(this.head, mapper));
		}

		public Options.Option<T> fold(BiFunction<T, T, T> folder) {
			return this.<Options.Option<T>>foldWithInitial(new Options.None<T>(), (current, element) -> {
				if (current instanceof Options.None<T>) {
					return new Options.Some<T>(element);
				}
				return current.map(inner -> folder.apply(inner, element));
			});
		}

		public Options.Option<T> next() {
			return this.head.next();
		}
	}

	public static <T> Stream<T> fromRef(T[] elements) {
		return new Stream<T>(new Heads.ArrayHead<T>(elements, elements.length));
	}

	public static <T> Stream<T> fromOption(Options.Option<T> option) {
		return new Stream<T>(switch (option) {
			case Options.None<T> _ -> new Heads.EmptyHead<T>();
			case Options.Some<T> v -> new Heads.SingletonHead<T>(v.value());
		});
	}

	static Stream<Integer> fromLength(int length) {
		return new Stream<Integer>(Heads.RangeHead.createRangeStream(length));
	}
}
