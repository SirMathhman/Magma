package magma;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lib {
	public sealed interface Result<T, X> permits Err, Ok {}

	public interface IOError {
		String display();
	}

	public interface Path {
		boolean exists();

		Result<String, IOError> readString();

		Options.Option<IOError> createDirectories();

		Options.Option<IOError> writeString(String output);

		Path getParent();

		Result<ArrayList<Path>, IOError> walk();

		String asString();

		Path relativize(Path path);

		Path resolveByPath(Path path);

		Stream<String> stream();

		Path getFileName();

		Path resolveByString(String name);
	}

	private interface Collector<T, C> {
		C createInitial();

		C fold(C current, T element);
	}

	@interface Actual {}

	public interface Head<T> {
		Options.Option<T> next();
	}

	record Ok<T, X>(T value) implements Result<T, X> {}

	record Err<T, X>(X error) implements Result<T, X> {}

	public record Stream<T>(Head<T> head) {
		<R> Stream<R> map(Function<T, R> mapper) {
			return new Stream<R>(() -> this.head.next().map(mapper));
		}

		public Stream<T> filter(Predicate<T> predicate) {
			final Head<T> sourceHead = this.head;
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

		public <C> C collect(Collector<T, C> collector) {
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
			return new Stream<R>(new FlatMapHead<T, R>(this.head, mapper));
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

	private static final class Array<T> {
		private final T[] ref;
		private final int capacity;
		private int length;

		private Array(T[] ref, int capacity) {
			this.ref = ref;
			this.capacity = capacity;
			this.length = 0;
		}

		private static <T> Array<T> alloc(int length) {
			return new Array<T>(MemUtils.malloc(length), length);
		}

		private boolean contains(T test) {
			Stream<T> tStream = this.stream();
			return tStream.collect(new AnyMatch<T>(element -> Objects.equals(element, test)));
		}

		private Stream<T> stream() {
			return new Stream<T>(new ArrayHead<T>(this.ref, this.length));
		}

		public void setNext(T element) {
			if (this.length < this.ref.length) {
				this.ref[this.length] = element;
				this.length++;
			}
		}

		public Options.Option<T> get(int index) {
			if (index < this.length) {
				return new Options.Some<T>(this.ref[index]);
			} else {
				return new Options.None<T>();
			}
		}

		public void setFirst(T element) {
			this.ref[0] = element;
		}
	}

	private static class MemUtils {
		@Actual
		private static <T> T[] malloc(int length) {
			return (T[]) new Object[length];
		}

		@Actual
		public static <T> void memCopy(Array<T> src, int srcPos, Array<T> dest, int destPos, int length) {
			System.arraycopy(src.ref, srcPos, dest.ref, destPos, length);
		}
	}

	public static final class ArrayList<T> {
		private Array<T> elements;

		private ArrayList(Array<T> elements) {
			this.elements = elements;
		}

		public ArrayList() {
			this(Array.alloc(10));
		}

		private void ensureCapacity(int minCapacity) {
			if (minCapacity <= this.elements.capacity) {
				return;
			}

			int newCapacity = this.elements.capacity * 2;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}

			Array<T> newElements = Array.alloc(newCapacity);
			MemUtils.memCopy(this.elements, 0, newElements, 0, (this.elements).length);
			newElements.length = (this.elements).length;
			this.elements = newElements;
		}

		public ArrayList<T> add(T element) {
			this.ensureCapacity((this.elements).length + 1);
			this.elements.setNext(element);
			return this;
		}

		public ArrayList<T> clear() {
			this.elements = Array.alloc(10);
			return this;
		}

		public int size() {
			return (this.elements).length;
		}

		public Stream<T> stream() {
			return new Stream<T>(new ListHead<T>(this));
		}

		public Options.Option<T> get(int index) {
			if (index < 0 || index >= (this.elements).length) {
				return new Options.None<T>();
			}

			return this.elements.get(index);
		}

		public boolean isEmpty() {
			return this.size() == 0;
		}

		public ArrayList<T> addFirst(T element) {
			this.ensureCapacity((this.elements).length + 1);
			// Shift all elements one position to the right
			MemUtils.memCopy(this.elements, 0, this.elements, 1, (this.elements).length);
			this.elements.setFirst(element);
			this.elements.length++;
			return this;
		}

		public ArrayList<T> addLast(T element) {
			return this.add(element);
		}

		public boolean contains(T element) {
			return this.elements.contains(element);
		}

		public Options.Option<T> getFirst() {
			return this.get(0);
		}

		public Options.Option<ArrayList<T>> subList(int start, int end) {
			if (start < 0 || end > (this.elements).length || start > end) {
				return new Options.None<ArrayList<T>>();
			}
			int subSize = end - start;
			Array<T> newElements = Array.alloc(Math.max(10, subSize));
			MemUtils.memCopy(this.elements, start, newElements, 0, subSize);
			newElements.length = subSize;
			return new Options.Some<ArrayList<T>>(new ArrayList<T>(newElements));
		}

		public ArrayList<T> addAll(ArrayList<T> elements) {
			int elementsSize = elements.size();
			this.ensureCapacity((this.elements).length + elementsSize);

			for (int i = 0; i < elementsSize; i++) {
				this.elements.setNext(elements.get(i).orElse(null));
			}

			return this;
		}

		public Options.Option<ArrayList<T>> addAllAt(int index, ArrayList<T> elements) {
			if (index < 0 || index > (this.elements).length) {
				return new Options.None<ArrayList<T>>();
			}

			int elementsSize = elements.size();
			this.ensureCapacity((this.elements).length + elementsSize);

			// Shift elements to the right to make room
			MemUtils.memCopy(this.elements, index, this.elements, index + elementsSize, (this.elements).length - index);

			// Copy inserted elements - need to use set with supplier since we're inserting in middle
			for (int i = 0; i < elementsSize; i++) {
				this.elements.ref[index + i] = elements.get(i).orElse(null);
			}

			this.elements.length += elementsSize;
			return new Options.Some<ArrayList<T>>(this);
		}

		public Options.Option<T> getLast() {
			return this.get((this.elements).length - 1);
		}

		public ArrayList<T> copy() {
			final ArrayList<T> list = new ArrayList<T>();
			if ((this.elements).length >= 0) {
				MemUtils.memCopy(this.elements, 0, list.elements, 0, (this.elements).length);
				list.elements.length = (this.elements).length;
			}
			return list;
		}
	}

	public static class Streams {
		public static <T> Stream<T> fromRef(T[] elements) {
			return new Stream<T>(new ArrayHead<T>(elements, elements.length));
		}

		public static <T> Stream<T> fromOption(Options.Option<T> option) {
			return new Stream<T>(switch (option) {
				case Options.None<T> _ -> new EmptyHead<T>();
				case Options.Some<T> v -> new SingletonHead<T>(v.value());
			});
		}

		static Stream<Integer> fromLength(int length) {
			return new Stream<Integer>(new RangeStream(length));
		}
	}

	private static class ListHead<T> implements Head<T> {
		private final ArrayList<T> self;
		private int index;

		public ListHead(ArrayList<T> self) {
			this.self = self;
			this.index = 0;
		}

		@Override
		public Options.Option<T> next() {
			if (this.index < this.self.size()) {
				return this.self.get(this.index++);
			}

			return new Options.None<T>();
		}
	}

	public static class ListCollector<T> implements Collector<T, ArrayList<T>> {
		public ListCollector() {}

		@Override
		public ArrayList<T> createInitial() {
			return new ArrayList<T>();
		}

		@Override
		public ArrayList<T> fold(ArrayList<T> current, T element) {
			return current.add(element);
		}
	}

	static class Joiner implements Collector<String, String> {
		private final String delimiter;

		public Joiner(String delimiter) {this.delimiter = delimiter;}

		public Joiner() {
			this("");
		}

		@Override
		public String createInitial() {
			return "";
		}

		@Override
		public String fold(String current, String element) {
			if (current.isEmpty()) {
				return element;
			}
			return current + this.delimiter + element;
		}
	}

	private static class ArrayHead<T> implements Head<T> {
		private final T[] elements;
		private final int length;
		private int counter;

		public ArrayHead(T[] elements, int length) {
			this.elements = elements;
			this.counter = 0;
			this.length = length;
		}

		@Override
		public Options.Option<T> next() {
			if (this.counter < this.length) {
				final T element = this.elements[this.counter];
				this.counter++;
				return new Options.Some<T>(element);
			} else {
				return new Options.None<T>();
			}
		}
	}

	private record AnyMatch<T>(Predicate<T> predicate) implements Collector<T, Boolean> {
		@Override
		public Boolean createInitial() {
			return false;
		}

		@Override
		public Boolean fold(Boolean current, T element) {
			return current || this.predicate.test(element);
		}
	}

	private static class EmptyHead<T> implements Head<T> {
		@Override
		public Options.Option<T> next() {
			return new Options.None<T>();
		}
	}

	private static class SingletonHead<T> implements Head<T> {
		private final T value;
		private boolean retrieved;

		public SingletonHead(T value) {
			this.value = value;
			this.retrieved = false;
		}

		@Override
		public Options.Option<T> next() {
			if (this.retrieved) {
				return new Options.None<T>();
			}
			this.retrieved = true;
			return new Options.Some<T>(this.value);
		}
	}

	private static class RangeStream implements Head<Integer> {
		private final int length;
		private int index;

		public RangeStream(int length) {
			this.length = length;
			this.index = 0;
		}

		@Override
		public Options.Option<Integer> next() {
			if (this.index < this.length) {
				final int preserve = this.index;
				this.index++;
				return new Options.Some<Integer>(preserve);
			} else {
				return new Options.None<Integer>();
			}
		}
	}

	private static class FlatMapHead<T, R> implements Head<R> {
		private final Head<T> sourceHead;
		private final Function<T, Stream<R>> mapper;
		private Options.Option<Stream<R>> currentStream;

		public FlatMapHead(Head<T> sourceHead, Function<T, Stream<R>> mapper) {
			this.sourceHead = sourceHead;
			this.mapper = mapper;
			this.currentStream = new Options.None<Stream<R>>();
		}

		@Override
		public Options.Option<R> next() {
			while (true) {
				// Try to get next element from current inner stream
				if (this.currentStream instanceof Options.Some<Stream<R>>(Stream<R> stream)) {
					Options.Option<R> nextValue = stream.next();
					if (nextValue instanceof Options.Some<R> _) {
						return nextValue;
					}
					// Current stream is exhausted, move to next
					this.currentStream = new Options.None<Stream<R>>();
				}

				// Get next element from source and map it to a stream
				Options.Option<T> nextSource = this.sourceHead.next();
				if (nextSource instanceof Options.Some<T>(T value)) {
					this.currentStream = new Options.Some<Stream<R>>(this.mapper.apply(value));
				} else {
					// No more source elements
					return new Options.None<R>();
				}
			}
		}
	}
}
