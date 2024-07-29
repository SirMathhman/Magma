package magma.api;

import java.util.Optional;

public class Err<T, E> implements Result<T, E> {
    private final E error;

    public Err(E error) {
        this.error = error;
    }

    @Override
    public Optional<T> findValue() {
        return Optional.empty();
    }
}
