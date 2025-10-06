package magma.list;

import magma.Tuple;
import magma.option.Option;

import java.util.Arrays;
import java.util.Comparator;

public interface List<T> {
	@SafeVarargs
	static <T> List<T> of(T... elements) {
		return new ArrayList<T>(new java.util.ArrayList<T>(Arrays.asList(elements)));
	}

	Stream<T> stream();

	List<T> addLast(T element);

	List<T> copy();

	List<T> addAll(List<T> others);

	int size();

	T getOrNull(int index);

	boolean isEmpty();

	Option<T> getLast();

	T getLastOrNull();

	List<T> sort(Comparator<T> comparator);

	default List<T> push(T element) {
		return addLast(element);
	}

	Option<Tuple<List<T>, T>> pop();

	Option<T> getFirst();

	T getFirstOrNull();

	List<T> subListOrEmpty(int start, int end);
}
