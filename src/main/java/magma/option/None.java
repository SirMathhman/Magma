package magma.option;

import magma.list.Iter;

/** Represents the absence of a value. */
public final class None<T> implements Option<T> {
    public None() {
    }

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public Iter<T> toIter() {
        return new OptionIter<>(this);
    }
}
