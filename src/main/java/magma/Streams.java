package magma;

import magma.Collectors.Collector;
import magma.Functions.BiFunction;
import magma.Functions.Function;
import magma.Heads.ArrayHead;
import magma.Heads.EmptyHead;
import magma.Heads.FlatMapHead;
import magma.Heads.RangeHead;
import magma.Heads.SingletonHead;
import magma.Main.Tuple;
import magma.Options.None;
import magma.Options.Option;
import magma.Options.Some;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Streams {
	public record Stream<T>(Supplier<Option<T>> head) {
		<R> Stream<R> map(Function<T, R> mapper) {
			return new Stream<R>(() -> this.head.get().map(mapper));
		}

		public Stream<T> filter(Predicate<T> predicate) {
			return new Stream<T>(() -> {
				while (true) {
					Option<T> nextValue = this.head.get();
					if (nextValue instanceof Some<T>(T value)) {
						if (predicate.test(value)) {
							return new Some<T>(value);
						}
						// Continue to next element
					} else {
						return new None<T>();
					}
				}
			});
		}

		public <C> C collect(Collector<T, C> collector) {
			return this.foldWithInitial(collector.createInitial(), collector::fold);
		}

		public <C> C foldWithInitial(C initial, BiFunction<C, T, C> folder) {
			C accumulator = initial;
			Option<T> current = this.head.get();
			while (current instanceof Some<T>(T value)) {
				accumulator = folder.apply(accumulator, value);
				current = this.head.get();
			}
			return accumulator;
		}

		public <R> Stream<R> flatMap(Function<T, Stream<R>> mapper) {
			return new Stream<R>(new FlatMapHead<T, R>(this.head, mapper));
		}

		public Option<T> fold(BiFunction<T, T, T> folder) {
			return this.<Option<T>>foldWithInitial(new None<T>(), (current, element) -> {
				if (current instanceof None<T>) {
					return new Some<T>(element);
				}
				return current.map(inner -> folder.apply(inner, element));
			});
		}

		public Option<T> next() {
			return this.head.get();
		}

		public <R> Stream<Tuple<T, R>> zip(Stream<R> stream) {
			return new Stream<Tuple<T, R>>(() -> this.head.get().and(stream::next));
		}
	}

	public static <T> Stream<T> fromRef(T[] elements) {
		return new Stream<T>(new ArrayHead<T>(elements, elements.length));
	}

	static Stream<Integer> fromLength(int length) {
		return new Stream<Integer>(RangeHead.createRangeStream(length));
	}

	public static <T> Stream<T> fromOption(Option<T> option) {
		return new Stream<T>(switch (option) {
			case None<T> _ -> new EmptyHead<T>();
			case Some<T> v -> new SingletonHead<T>(v.value());
		});
	}
}
