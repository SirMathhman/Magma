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

	Option<T> get(int index);

	boolean isEmpty();

	Option<T> getLast();

	List<T> sort(Comparator<T> comparator);

	Option<Tuple<List<T>, T>> removeLast();

	Option<T> getFirst();

	List<T> subListOrEmpty(int start, int end);
}
