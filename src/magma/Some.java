package magma;

import java.util.function.Function;
import java.util.function.Supplier;

record Some<T>(T value) implements Option<T> {
    @Override
    public <R> Option<R> map(Function<T, R> mapper) {
        return new Some<>(mapper.apply(value));
    }

    @Override
    public T orElseGet(Supplier<T> other) {
        return value;
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
    public T orElse(T other) {
        return value;
    }

    @Override
    public Option<T> or(Supplier<Option<T>> other) {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
        return other.get().map(otherValue -> new Tuple<>(value, otherValue));
    }
}
