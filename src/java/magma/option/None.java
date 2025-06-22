package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;

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
    public <Return> Option<Return> flatMap(final Function<T, Option<Return>> mapper) {
        return new None<>();
    }
}
