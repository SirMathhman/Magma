package magma.option;

import java.util.function.Consumer;

public interface Option<T> {
    static <T> Option<T> of(final T value) {
        return new Some<T>(value);
    }

    static <T> Option<T> empty() {
        return new None<T>();
    }

    void ifPresent(Consumer<T> consumer);
}
