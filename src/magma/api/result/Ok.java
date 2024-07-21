package magma.api.result;

import java.util.Optional;

public record Ok<T, E extends Exception>(T value) implements Result<T, E> {
    @Override
    public Optional<T> findValue() {
        return Optional.of(value);
    }

    @Override
    public T $() throws E {
        return value;
    }
}
