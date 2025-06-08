package magma.option;

import magma.list.Iter;

/**
 * Minimal optional value container with distinct variants.
 */
public interface Option<T> {
    boolean isSome();

    T get();

    Iter<T> toIter();
}
