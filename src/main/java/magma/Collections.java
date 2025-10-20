package magma;

import magma.Collectors.AnyMatch;
import magma.Heads.ArrayHead;
import magma.Heads.ListHead;
import magma.Options.None;
import magma.Options.Option;
import magma.Options.Some;
import magma.Streams.Stream;

import java.util.Objects;

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

		public ArrayList<T> addAll(ArrayList<T> elements) {
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

			// Copy inserted elements - need to use set with supplier since we're inserting in middle
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
			if ((this.elements).length >= 0) {
				this.elements.copyTo(0, list.elements, 0, (this.elements).length);
				list.elements.length = (this.elements).length;
			}
			return list;
		}
	}
}
