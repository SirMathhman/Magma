package magma.result;

import java.util.function.Function;

/**
 * Result variant representing success.
 * See {@code Err} for the failure case.
 */
public final class Ok<T, X> implements Result<T, X> {
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public boolean isErr() {
        return false;
    }

    @Override
    public <U> Result<U, X> mapValue(Function<? super T, ? extends U> mapper) {
        return new Ok<>(mapper.apply(value));
    }

    @Override
    public <U> Result<U, X> flatMapValue(Function<? super T, Result<U, X>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
        return whenOk.apply(value);
    }

}
