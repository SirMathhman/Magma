package magma;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record None<T>() implements Optional<T> {
    @Override
    public void ifPresent(final Consumer<T> consumer) {
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Optional<T> or(final Supplier<Optional<T>> other) {
        return other.get();
    }

    @Override
    public T orElseGet(final Supplier<T> other) {
        return other.get();
    }

    @Override
    public <R> Optional<R> map(final Function<T, R> mapper) {
        return new None<>();
    }

    @Override
    public <R> Optional<R> flatMap(final Function<T, Optional<R>> mapper) {
        return new None<>();
    }

    @Override
    public T orElse(final T other) {
        return other;
    }

    @Override
    public Optional<T> filter(final Predicate<T> predicate) {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Tuple<Boolean, T> toTuple(final T other) {
        return new Tuple<>(false, other);
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }
}
