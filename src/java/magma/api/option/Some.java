package magma.api.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record Some<T>(T value) implements Option<T> {
    @Override
    public void ifPresent(final Consumer<T> consumer) {
        consumer.accept(value);
    }

    @Override
    public T orElse(final T other) {
        return value;
    }

    @Override
    public <Return> Option<Return> map(final Function<T, Return> mapper) {
        return new Some<>(mapper.apply(value));
    }

    @Override
    public T orElseGet(final Supplier<T> other) {
        return value;
    }

    @Override
    public Option<T> filter(final Predicate<T> predicate) {
        return predicate.test(value) ? this : new None<>();
    }

    @Override
    public boolean isPresent() {
        return true;
    }
}
