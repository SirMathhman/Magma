package magma.api;

import java.util.Optional;
import java.util.function.Function;

public class Ok<T, X> implements Result<T, X> {
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    @Override
    public <R> Result<R, X> map(Function<T, R> mapper) {
        return new Ok<>(mapper.apply(this.value));
    }

    @Override
    public <R> Result<R, X> flatMap(Function<T, Result<R, X>> mapper) {
        return mapper.apply(this.value);
    }

    @Override
    public Optional<T> findValue() {
        return Optional.of(this.value);
    }
}
