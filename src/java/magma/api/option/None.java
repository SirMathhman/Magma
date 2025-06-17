package magma.api.option;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class None<T> implements Option<T> {
    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public Option<T> or(Supplier<Option<T>> other) {
        return other.get();
    }

    @Override
    public T orElse(T other) {
        return other;
    }

    @Override
    public void ifPresent(Consumer<T> other) {
    }
}
