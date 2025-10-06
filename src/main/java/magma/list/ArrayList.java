package magma.list;

import magma.Tuple;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.Comparator;

public record ArrayList<T>(java.util.List<T> elements) implements List<T> {
	public ArrayList() {
		this(new java.util.ArrayList<T>());
	}

	@Override
	public Stream<T> stream() {
		return new HeadedStream<Integer>(new RangeHead(elements.size())).map(elements::get);
	}

	@Override
	public List<T> addLast(T element) {
		elements.add(element);
		return this;
	}

	@Override
	public List<T> copy() {
		return new ArrayList<T>(new java.util.ArrayList<T>(elements));
	}

	@Override
	public List<T> addAll(List<T> others) {
		return others.stream().<List<T>>fold(this, List::addLast);
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public T getOrNull(int index) {
		return elements.get(index);
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public T getLastOrNull() {
		if (elements.isEmpty()) return null;
		return elements.getLast();
	}

	@Override
	public List<T> sort(Comparator<T> comparator) {
		elements.sort(comparator);
		return this;
	}

	@Override
	public Option<Tuple<List<T>, T>> pop() {
		if (elements.isEmpty()) return new None<Tuple<List<T>, T>>();

		final T last = elements.removeLast();
		return new Some<Tuple<List<T>, T>>(new Tuple<List<T>, T>(this, last));
	}

	@Override
	public T getFirstOrNull() {
		if (elements.isEmpty()) return null;
		return elements.getFirst();
	}
}
