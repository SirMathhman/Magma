package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;

public record Some<T>(T value) implements Option<T> {
    @Override
    public void ifPresent(final Consumer<T> consumer) {
        consumer.accept(this.value);
    }

    @Override
    public T orElse(final T other) {
        return this.value;
    }

    @Override
    public <Return> Option<Return> flatMap(final Function<T, Option<Return>> mapper) {
        return mapper.apply(this.value);
    }
}
