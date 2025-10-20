package magma;

import magma.Collections.ArrayList;
import magma.Options.None;
import magma.Options.Option;
import magma.Options.Some;
import magma.Streams.Stream;

import java.util.function.Function;

public class Heads {
	public interface Head<T> {
		Option<T> next();
	}

	static class ListHead<T> implements Head<T> {
		private final ArrayList<T> self;
		private int index;

		public ListHead(ArrayList<T> self) {
			this.self = self;
			this.index = 0;
		}

		@Override
		public Option<T> next() {
			if (this.index < this.self.size()) {
				return this.self.get(this.index++);
			}

			return new None<T>();
		}
	}

	static class ArrayHead<T> implements Head<T> {
		private final T[] elements;
		private final int length;
		private int counter;

		public ArrayHead(T[] elements, int length) {
			this.elements = elements;
			this.counter = 0;
			this.length = length;
		}

		@Override
		public Option<T> next() {
			if (this.counter < this.length) {
				final T element = this.elements[this.counter];
				this.counter++;
				return new Some<T>(element);
			} else {
				return new None<T>();
			}
		}
	}

	static class SingletonHead<T> implements Head<T> {
		private final T value;
		private boolean retrieved;

		public SingletonHead(T value) {
			this.value = value;
			this.retrieved = false;
		}

		@Override
		public Option<T> next() {
			if (this.retrieved) {
				return new None<T>();
			}
			this.retrieved = true;
			return new Some<T>(this.value);
		}
	}

	static class FlatMapHead<T, R> implements Head<R> {
		private final Head<T> sourceHead;
		private final Function<T, Stream<R>> mapper;
		private Option<Stream<R>> currentStream;

		public FlatMapHead(Head<T> sourceHead, Function<T, Stream<R>> mapper) {
			this.sourceHead = sourceHead;
			this.mapper = mapper;
			this.currentStream = new None<Stream<R>>();
		}

		@Override
		public Option<R> next() {
			while (true) {
				// Try to get next element from current inner stream
				if (this.currentStream instanceof Some<Stream<R>>(Stream<R> stream)) {
					Option<R> nextValue = stream.next();
					if (nextValue instanceof Some<R> _) {
						return nextValue;
					}
					// Current stream is exhausted, move to next
					this.currentStream = new None<Stream<R>>();
				}

				// Get next element from source and map it to a stream
				Option<T> nextSource = this.sourceHead.next();
				if (nextSource instanceof Some<T>(T value)) {
					this.currentStream = new Some<Stream<R>>(this.mapper.apply(value));
				} else {
					// No more source elements
					return new None<R>();
				}
			}
		}
	}

	static class RangeHead implements Head<Integer> {
		private final int length;
		private int index;

		private RangeHead(int length) {
			this.length = length;
			this.index = 0;
		}

		public static RangeHead createRangeStream(int length) {
			return new RangeHead(length);
		}

		@Override
		public Option<Integer> next() {
			if (this.index < this.length) {
				final int preserve = this.index;
				this.index++;
				return new Some<Integer>(preserve);
			} else {
				return new None<Integer>();
			}
		}
	}

	static class EmptyHead<T> implements Head<T> {
		@Override
		public Option<T> next() {
			return new None<T>();
		}
	}
}
