package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record None<T>() implements Option<T> {
    @Override
    public void ifPresent(final Consumer<T> consumer) {
    }

    @Override
    public T orElse(final T other) {
        return other;
    }

    @Override
    public <Return> Option<Return> map(final Function<T, Return> mapper) {
        return new None<>();
    }

    @Override
    public T orElseGet(final Supplier<T> other) {
        return other.get();
    }
}