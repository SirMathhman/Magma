package magma.api;

import java.util.Optional;
import java.util.function.Function;

public class Err<T, X> implements Result<T, X> {
    private final X error;

    public Err(X error) {
        this.error = error;
    }

    @Override
    public <R> Result<R, X> map(Function<T, R> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public <R> Result<R, X> flatMap(Function<T, Result<R, X>> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public Optional<T> findValue() {
        return Optional.empty();
    }
}
