package magma;

import java.util.function.Function;
import java.util.function.Supplier;

final class None<T> implements Option<T> {
    @Override
    public <R> Option<R> map(Function<T, R> mapper) {
        return new None<>();
    }

    @Override
    public T orElseGet(Supplier<T> other) {
        return other.get();
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public T orElse(T other) {
        return other;
    }

    @Override
    public Option<T> or(Supplier<Option<T>> other) {
        return other.get();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public <R> Option<Tuple<T, R>> and(Supplier<Option<R>> other) {
        return new None<>();
    }
}
