package magma.list;

import magma.Tuple;
import magma.option.Option;

import java.util.Arrays;
import java.util.Comparator;

public interface List<T> {
	static <T> List<T> of(T... elements) {
		return new ArrayList<>(new java.util.ArrayList<>(Arrays.asList(elements)));
	}

	Stream<T> stream();

	List<T> addLast(T element);

	List<T> copy();

	List<T> addAll(List<T> others);

	int size();

	T getOrNull(int index);

	boolean isEmpty();

	T getLastOrNull();

	List<T> sort(Comparator<T> comparator);

	default List<T> push(T element) {
		return addLast(element);
	}

	Option<Tuple<List<T>, T>> pop();

	T getFirstOrNull();
}
