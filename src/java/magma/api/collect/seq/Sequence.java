package magma.api.collect.seq;

import magma.api.collect.fold.Folding;

public interface Sequence<T> extends Folding<T> {
    int size();

    T get(int index);
}
