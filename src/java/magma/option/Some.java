package magma.option;

import java.util.function.Consumer;

public record Some<T>(T value) implements Option<T> {
    @Override
    public void ifPresent(final Consumer<T> consumer) {
        consumer.accept(this.value);
    }

    @Override
    public T orElse(final T other) {
        return this.value;
    }
}
