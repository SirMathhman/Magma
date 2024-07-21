package magma.api.result;

import java.util.Optional;

public record Err<T, E extends Exception>(E value) implements Result<T, E> {
    @Override
    public Optional<T> findValue() {
        return Optional.empty();
    }

    @Override
    public T $() throws E {
        throw value;
    }
}
