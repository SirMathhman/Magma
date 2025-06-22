package magma.option;

import java.util.function.Consumer;

public record None<T>() implements Option<T> {
    @Override
    public void ifPresent(final Consumer<T> consumer) {
    }

    @Override
    public T orElse(final T other) {
        return other;
    }

}
