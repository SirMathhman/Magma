package magma.result;

import java.util.function.Function;

/**
 * Result variant representing failure.
 * See {@code Ok} for the success case.
 */
public final class Err<T, X> implements Result<T, X> {
    private final X error;

    public Err(X error) {
        this.error = error;
    }

    public X error() {
        return error;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isErr() {
        return true;
    }

    @Override
    public <U> Result<U, X> mapValue(Function<? super T, ? extends U> mapper) {
        return new Err<>(error);
    }

    @Override
    public <U> Result<U, X> flatMapValue(Function<? super T, Result<U, X>> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
        return whenErr.apply(error);
    }

}
