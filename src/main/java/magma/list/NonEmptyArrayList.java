package magma.list;

import magma.Tuple;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.Comparator;

/**
 * Implementation of NonEmptyList backed by a List.
 * This class does not verify non-emptiness at construction—callers must ensure
 * correctness.
 */
public record NonEmptyArrayList<T>(List<T> backing) implements NonEmptyList<T> {

	@Override
	public List<T> toList() {
		return backing;
	}

	@Override
	public Stream<T> stream() {
		return backing.stream();
	}

	@Override
	public NonEmptyList<T> addLast(T element) {
		return new NonEmptyArrayList<T>(backing.addLast(element));
	}

	@Override
	public NonEmptyList<T> copy() {
		return new NonEmptyArrayList<T>(backing.copy());
	}

	@Override
	public NonEmptyList<T> addAll(List<T> others) {
		return new NonEmptyArrayList<T>(backing.addAll(others));
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public Option<T> get(int index) {
		return backing.get(index);
	}

	@Override
	public T getLast() {
		return backing.getLast().orElse(null); // Safe because non-empty guarantee
	}

	@Override
	public T getFirst() {
		return backing.getFirst().orElse(null); // Safe because non-empty guarantee
	}

	@Override
	public NonEmptyList<T> sort(Comparator<T> comparator) {
		return new NonEmptyArrayList<T>(backing.sort(comparator));
	}

	@Override
	public Option<Tuple<List<T>, T>> removeLast() {
		return switch (backing.removeLast()) {
			case Some<Tuple<List<T>, T>>(Tuple<List<T>, T> tuple) -> {
				if (tuple.left().isEmpty()) {
					// Only one element left, return None
					yield new None<Tuple<List<T>, T>>();
				}
				yield new Some<Tuple<List<T>, T>>(tuple);
			}
			case None<Tuple<List<T>, T>> _ -> new None<Tuple<List<T>, T>>();
		};
	}
}
