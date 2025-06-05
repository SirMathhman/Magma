package magma.option;

import magma.Tuple;

import java.util.function.Function;

/**
 * Option variant representing presence of a value.
 */
public final class Some<T> implements Option<T> {
    private final T value;

    public Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
        return new Some<>(mapper.apply(value));
    }

    @Override
    public <U> Option<U> flatMap(Function<? super T, Option<U>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public Option<T> orElse(Option<T> other) {
        return this;
    }

    @Override
    public Tuple<Boolean, T> toTuple(T defaultValue) {
        return new Tuple<>(true, value);
    }
}
