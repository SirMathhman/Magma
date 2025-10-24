package magma;

import magma.Collectors.AllMatch;
import magma.Collectors.AnyMatch;
import magma.Collectors.ListCollector;
import magma.Collectors.MapCollector;
import magma.Functions.BiFunction;
import magma.Heads.ArrayHead;
import magma.Heads.ListHead;
import magma.Options.None;
import magma.Options.Option;
import magma.Options.Some;
import magma.Streams.Stream;
import magma.Utils.Tuple;

import java.util.Objects;
import java.util.function.Function;

public class Collections {
	static final class Array<T> {
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

		public void copyTo(int srcPos, Array<T> dest, int destPos, int length) {
			System.arraycopy(this.ref, srcPos, dest.ref, destPos, length);
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

		public Option<T> get(int index) {
			if (index < this.length) {
				return new Some<T>(this.ref[index]);
			} else {
				return new None<T>();
			}
		}

		public void setFirst(T element) {
			this.ref[0] = element;
		}
	}

	public static class ListMap<K, V> {
		private ArrayList<Tuple<K, V>> entries;

		public ListMap() {
			this(new ArrayList<Tuple<K, V>>());
		}

		public ListMap(ArrayList<Tuple<K, V>> entries) {
			this.entries = entries;
		}

		public ListMap<K, V> put(K key, V value) {
			this.entries = this.entries
					.stream()
					.filter(entry -> !entry.left().equals(key))
					.collect(new ListCollector<Tuple<K, V>>())
					.addLast(new Tuple<K, V>(key, value));

			return this;
		}

		public ListMap<K, V> copy() {
			return new ListMap<K, V>(this.entries.copy());
		}

		public Option<V> get(String key) {
			return this.entries.stream().filter(tuple -> tuple.left().equals(key)).map(Tuple::right).next();
		}

		public boolean isEmpty() {
			return this.entries.isEmpty();
		}

		public Stream<Tuple<K, V>> stream() {
			return this.entries.stream();
		}

		public ListMap<K, V> removeKey(K key) {
			this.entries = this.entries.stream().filter(entry -> !entry.left().equals(key))
					.collect(new ListCollector<Tuple<K, V>>());
			return this;
		}

		public ListMap<K, V> putAll(ListMap<K, V> other) {
			return other.entries.stream().foldWithInitial(this, (first, second) -> first.put(second.left(), second.right()));
		}

		public <R> ListMap<K, R> mapValues(BiFunction<K, V, R> mapper) {
			return this
					.stream()
					.map(tuple -> new Tuple<K, R>(tuple.left(), mapper.apply(tuple.left(), tuple.right())))
					.collect(new MapCollector<K, R>());
		}

		public ArrayList<K> keys() {
			return this.stream().map(Tuple::left).collect(new ListCollector<K>());
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

		public static <T> ArrayList<T> from(T... elements) {
			return Streams.fromRef(elements).collect(new ListCollector<T>());
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
			this.elements.copyTo(0, newElements, 0, (this.elements).length);
			newElements.length = (this.elements).length;
			this.elements = newElements;
		}

		public ArrayList<T> addLast(T element) {
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

		public Option<T> get(int index) {
			if (index < 0 || index >= (this.elements).length) {
				return new None<T>();
			}

			return this.elements.get(index);
		}

		public boolean isEmpty() {
			return this.size() == 0;
		}

		public ArrayList<T> addFirst(T element) {
			this.ensureCapacity((this.elements).length + 1);
			// Shift all elements one position to the right
			this.elements.copyTo(0, this.elements, 1, (this.elements).length);
			this.elements.setFirst(element);
			this.elements.length++;
			return this;
		}

		public boolean contains(T element) {
			return this.elements.contains(element);
		}

		public Option<T> getFirst() {
			return this.get(0);
		}

		public Option<ArrayList<T>> subList(int start, int end) {
			if (start < 0 || end > (this.elements).length || start > end) {
				return new None<ArrayList<T>>();
			}
			int subSize = end - start;
			Array<T> newElements = Array.alloc(Math.max(10, subSize));
			this.elements.copyTo(start, newElements, 0, subSize);
			newElements.length = subSize;
			return new Some<ArrayList<T>>(new ArrayList<T>(newElements));
		}

		public ArrayList<T> addAllLast(ArrayList<T> elements) {
			int elementsSize = elements.size();
			this.ensureCapacity((this.elements).length + elementsSize);

			for (int i = 0; i < elementsSize; i++) {
				this.elements.setNext(elements.get(i).orElse(null));
			}

			return this;
		}

		public Option<ArrayList<T>> addAllAt(int index, ArrayList<T> elements) {
			if (index < 0 || index > (this.elements).length) {
				return new None<ArrayList<T>>();
			}

			int elementsSize = elements.size();
			this.ensureCapacity((this.elements).length + elementsSize);

			// Shift elements to the right to make room
			this.elements.copyTo(index, this.elements, index + elementsSize, (this.elements).length - index);

			// Copy inserted elements - need to use set with supplier since we're inserting
			// in middle
			for (int i = 0; i < elementsSize; i++) {
				this.elements.ref[index + i] = elements.get(i).orElse(null);
			}

			this.elements.length += elementsSize;
			return new Some<ArrayList<T>>(this);
		}

		public Option<T> getLast() {
			return this.get((this.elements).length - 1);
		}

		public ArrayList<T> copy() {
			final ArrayList<T> list = new ArrayList<T>();
			list.ensureCapacity(this.elements.length);

			if ((this.elements).length >= 0) {
				this.elements.copyTo(0, list.elements, 0, (this.elements).length);
				list.elements.length = (this.elements).length;
			}
			return list;
		}

		public boolean equalsTo(ArrayList<T> other) {
			if (this.size() == other.size()) {
				return this
						.stream()
						.zip(other.stream())
						.collect(new AllMatch<Tuple<T, T>>(ttTuple -> ttTuple.left().equals(ttTuple.right())));
			}

			return false;
		}

		public ArrayList<T> removeValue(T element) {
			for (int i = 0; i < this.elements.length; i++) {
				Option<T> current = this.elements.get(i);
				if (current instanceof Some<T>(T value) && Objects.equals(value, element)) {
					// Shift elements left to fill the gap
					this.elements.copyTo(i + 1, this.elements, i, this.elements.length - i - 1);
					this.elements.length--;
					break;
				}
			}
			return this;
		}

		public ArrayList<T> join(ArrayList<T> other) {
			return this.stream().filter(other::contains).collect(new ListCollector<T>());
		}

		public Option<Tuple<T, ArrayList<T>>> removeLast() {
			if (this.isEmpty()) {
				return new None<>();
			}

			Option<T> lastElement = this.getLast();
			if (lastElement instanceof Some<T>(T value)) {
				this.elements.length--;
				return new Some<>(new Tuple<>(value, this));
			}
			return new None<>();
		}

		public ArrayList<T> mapLast(Function<T, T> mapper) {
			if (this.isEmpty()) {
				return this;
			}

			int lastIndex = this.elements.length - 1;
			Option<T> lastElement = this.elements.get(lastIndex);
			if (lastElement instanceof Some<T>(T value)) {
				this.elements.ref[lastIndex] = mapper.apply(value);
			}
			return this;
		}
	}
}
