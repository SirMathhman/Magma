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
		return others.stream().fold(getThis(), List::addLast);
	}

	private List<T> getThis() {
		return this;
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public Option<T> get(int index) {
		if (index >= 0 && index < elements.size()) return new Some<T>(elements.get(index));
		else return new None<T>();
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public Option<T> getLast() {
		if (elements.isEmpty()) return new None<T>();
		return new Some<T>(elements.getLast());
	}

	@Override
	public List<T> sort(Comparator<T> comparator) {
		elements.sort(comparator);
		return this;
	}

	@Override
	public Option<Tuple<List<T>, T>> removeLast() {
		if (elements.isEmpty()) return new None<Tuple<List<T>, T>>();

		final T last = elements.removeLast();
		return new Some<Tuple<List<T>, T>>(new Tuple<List<T>, T>(this, last));
	}

	@Override
	public Option<T> getFirst() {
		if (elements.isEmpty()) return new None<T>();
		return new Some<T>(elements.getFirst());
	}

	@Override
	public List<T> subListOrEmpty(int start, int end) {
		return new ArrayList<T>(elements.subList(start, end));
	}
}
