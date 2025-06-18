package magma.api.collect.seq;

import magma.api.collect.fold.Foldable;

public interface Sequence<T> extends Foldable<T> {
    int size();

    T get(int index);
}
