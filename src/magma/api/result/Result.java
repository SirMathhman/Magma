package magma.api.result;

import java.util.Optional;

public interface Result<T, E extends Exception> {
    Optional<T> findValue();

    T $() throws E;
}
