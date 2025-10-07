package magma.list;

import magma.option.Option;
import magma.option.Some;

/**
 * Array-based implementation of NonEmptyList.
 *
 * @param <T> the type of elements in this list
 */
public record ArrayNonEmptyList<T>(T head, List<T> tail) implements NonEmptyList<T> {

	@Override
	public T first() {
		return head;
	}

	@Override
	public T last() {
		final Option<T> tailLast = tail.getLast();
		if (tailLast instanceof Some<T>(T value))
			return value;
		return head;
	}

	@Override
	public List<T> rest() {
		return tail;
	}

	@Override
	public Stream<T> stream() {
		final List<T> combined = new ArrayList<T>().addLast(head).addAll(tail);
		return combined.stream();
	}

	@Override
	public NonEmptyList<T> addLast(T element) {
		return new ArrayNonEmptyList<>(head, tail.addLast(element));
	}

	@Override
	public int size() {
		return 1 + tail.size();
	}

	@Override
	public Option<T> get(int index) {
		if (index == 0)
			return new Some<>(head);
		return tail.get(index - 1);
	}

	@Override
	public List<T> toList() {
		return new ArrayList<T>().addLast(head).addAll(tail);
	}
}
