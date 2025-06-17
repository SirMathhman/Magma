package magma.api.option;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Some<T> implements Option<T> {
    private final T value;

    public Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T get() {
        return this.value;
    }

    @Override
    public Option<T> or(Supplier<Option<T>> other) {
        return this;
    }

    @Override
    public T orElse(T other) {
        return this.value;
    }

    @Override
    public void ifPresent(Consumer<T> other) {
        other.accept(this.value);
    }
}
