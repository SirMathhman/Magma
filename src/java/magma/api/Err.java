package magma.api;

import java.util.function.Function;

public class Err<T, X> implements Result<T, X> {
    private final X error;

    public Err(X error) {
        this.error = error;
    }

    @Override
    public <R> Result<R, X> mapValue(Function<T, R> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public <R> Result<R, X> flatMap(Function<T, Result<R, X>> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public <R> Result<T, R> mapErr(Function<X, R> mapper) {
        return new Err<>(mapper.apply(this.error));
    }

    @Override
    public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
        return whenErr.apply(this.error);
    }
}
