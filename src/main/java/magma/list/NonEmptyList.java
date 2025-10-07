package magma.list;

import magma.Tuple;
import magma.option.Option;
import magma.option.Some;

import java.util.Comparator;

/**
 * A list that guarantees at least one element is present.
 * This is a semantic refinement of List<T> that eliminates the need for
 * isEmpty() checks and Option wrapping for operations like getFirst() and
 * getLast().
 */
public interface NonEmptyList<T> {
	/**
	 * Creates a NonEmptyList from a List, wrapping it.
	 * The caller must ensure the list is non-empty; this is unchecked.
	 *
	 * @param list the backing list (must be non-empty)
	 * @param <T>  the element type
	 * @return a NonEmptyList view of the list
	 */
	static <T> NonEmptyList<T> from(List<T> list) {
		return new NonEmptyArrayList<T>(list);
	}

	/**
	 * Creates a NonEmptyList from one or more elements.
	 *
	 * @param first the first element (required)
	 * @param rest  additional elements (optional)
	 * @param <T>   the element type
	 * @return a NonEmptyList containing the elements
	 */
	@SafeVarargs
	static <T> NonEmptyList<T> of(T first, T... rest) {
		List<T> list = new ArrayList<T>();
		list = list.addLast(first);
		for (T element : rest) {
			list = list.addLast(element);
		}
		return new NonEmptyArrayList<T>(list);
	}

	/**
	 * Returns the underlying List representation.
	 *
	 * @return the list
	 */
	List<T> toList();

	/**
	 * Returns a stream over the elements.
	 *
	 * @return the stream
	 */
	Stream<T> stream();

	/**
	 * Adds an element to the end of the list.
	 *
	 * @param element the element to add
	 * @return a new NonEmptyList with the element added
	 */
	NonEmptyList<T> addLast(T element);

	/**
	 * Returns a copy of this list.
	 *
	 * @return a copy
	 */
	NonEmptyList<T> copy();

	/**
	 * Adds all elements from another list.
	 *
	 * @param others the elements to add
	 * @return a new NonEmptyList with all elements
	 */
	NonEmptyList<T> addAll(List<T> others);

	/**
	 * Returns the number of elements.
	 *
	 * @return the size (always >= 1)
	 */
	int size();

	/**
	 * Returns the element at the given index.
	 *
	 * @param index the index
	 * @return Some(element) if index is valid, None otherwise
	 */
	Option<T> get(int index);

	/**
	 * Returns the last element (guaranteed to exist).
	 *
	 * @return the last element
	 */
	T getLast();

	/**
	 * Returns the first element (guaranteed to exist).
	 *
	 * @return the first element
	 */
	T getFirst();

	/**
	 * Sorts the list using the given comparator.
	 *
	 * @param comparator the comparator
	 * @return a sorted NonEmptyList
	 */
	NonEmptyList<T> sort(Comparator<T> comparator);

	/**
	 * Removes the last element and returns the remaining list (if any) and the
	 * removed element.
	 *
	 * @return Some(remaining list, last element) if size > 1, or None if size == 1
	 */
	Option<Tuple<List<T>, T>> removeLast();
}
