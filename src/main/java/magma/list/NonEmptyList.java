package magma.list;

import magma.option.None;
import magma.option.Option;

/**
 * A list that is guaranteed to have at least one element.
 * This provides a static invariant that eliminates the need for Option types
 * when accessing the first or last element.
 *
 * @param <T> the type of elements in this list
 */
public interface NonEmptyList<T> {
	/**
	 * Creates a NonEmptyList with a single element.
	 */
	static <T> NonEmptyList<T> of(T element) {
		return new ArrayNonEmptyList<>(element, new ArrayList<>());
	}

	/**
	 * Creates a NonEmptyList from a first element and additional elements.
	 */
	@SafeVarargs
	static <T> NonEmptyList<T> of(T first, T... others) {
		return new ArrayNonEmptyList<>(first, List.of(others));
	}

	/**
	 * Creates a NonEmptyList from an existing List if it's not empty.
	 * Returns None if the list is empty.
	 */
	static <T> Option<NonEmptyList<T>> fromList(List<T> list) {
		if (list.isEmpty())
			return new None<>();

		return list.getFirst().map(head -> {
			final List<T> tail = list.subListOrEmpty(1, list.size());
			return new ArrayNonEmptyList<>(head, tail);
		});
	}

	/**
	 * Returns the first element (always present).
	 */
	T first();

	/**
	 * Returns the last element (always present).
	 */
	T last();

	/**
	 * Returns the rest of the list after the first element.
	 * This may be empty.
	 */
	List<T> rest();

	/**
	 * Returns a stream of all elements.
	 */
	Stream<T> stream();

	/**
	 * Adds an element to the end of the list.
	 */
	NonEmptyList<T> addLast(T element);

	/**
	 * Returns the number of elements in the list (always >= 1).
	 */
	int size();

	/**
	 * Gets the element at the specified index.
	 */
	Option<T> get(int index);

	/**
	 * Converts this NonEmptyList to a regular List.
	 */
	List<T> toList();
}
