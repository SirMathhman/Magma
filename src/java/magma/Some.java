package magma;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record Some<T>(T value) implements Optional<T> {
    @Override
    public void ifPresent(final Consumer<T> consumer) {
        consumer.accept(this.value);
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public Optional<T> or(final Supplier<Optional<T>> other) {
        return this;
    }

    @Override
    public T orElseGet(final Supplier<T> other) {
        return this.value;
    }

    @Override
    public <R> Optional<R> map(final Function<T, R> mapper) {
        return new Some<>(mapper.apply(this.value));
    }

    @Override
    public <R> Optional<R> flatMap(final Function<T, Optional<R>> mapper) {
        return mapper.apply(this.value);
    }

    @Override
    public T orElse(final T other) {
        return this.value;
    }

    @Override
    public Optional<T> filter(final Predicate<T> predicate) {
        if (predicate.test(this.value))
            return this;
        return new None<>();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Tuple<Boolean, T> toTuple(final T other) {
        return new Tuple<>(true, this.value);
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(this.value);
    }
}
