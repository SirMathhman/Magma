package magma.option;

import magma.Tuple;

import java.util.function.Function;

/**
 * Option variant representing absence of a value.
 */
public final class None<T> implements Option<T> {
    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T get() {
        throw new java.util.NoSuchElementException("No value present");
    }

    @Override
    public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
        return new None<>();
    }

    @Override
    public <U> Option<U> flatMap(Function<? super T, Option<U>> mapper) {
        return new None<>();
    }

    @Override
    public Option<T> orElse(Option<T> other) {
        return other;
    }

    @Override
    public Tuple<Boolean, T> toTuple(T defaultValue) {
        return new Tuple<>(false, defaultValue);
    }
}
