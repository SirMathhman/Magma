package magma.option;

import magma.list.Iter;

/** Holds a present value. */
public final class Some<T> implements Option<T> {
    private final T value;

    public Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public Iter<T> toIter() {
        return new OptionIter<>(this);
    }
}
