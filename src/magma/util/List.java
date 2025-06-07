package magma.util;

import java.util.function.Function;

/**
 * Immutable list abstraction used by the AST and utilities.
 */
public interface List<T> {
    List<T> add(T element);

    Iterator<T> iter();

    List<T> addAll(List<T> elements);

    Option<Tuple<List<T>, T>> popLast();

    boolean isEmpty();

    T get(int index);

    Iterator<Tuple<Integer, T>> iterWithIndex();

    Iterator<T> iterReversed();

    List<T> mapLast(Function<T, T> mapper);
}
