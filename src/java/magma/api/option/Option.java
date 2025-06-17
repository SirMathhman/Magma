package magma.api.option;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Option<T> {
    static <T> Option<T> empty() {
        return new None<>();
    }

    static <T> Option<T> of(T value) {
        return new Some<>(value);
    }

    boolean isPresent();

    T get();

    Option<T> or(Supplier<Option<T>> other);

    T orElse(T other);

    void ifPresent(Consumer<T> other);
}
