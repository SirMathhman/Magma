package magma.api.result;

import java.util.function.Function;

public class Ok<T, X> implements Result<T, X> {
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    @Override
    public <R> Result<R, X> mapValue(Function<T, R> mapper) {
        return new Ok<>(mapper.apply(this.value));
    }

    @Override
    public <R> Result<R, X> flatMap(Function<T, Result<R, X>> mapper) {
        return mapper.apply(this.value);
    }

    @Override
    public <R> Result<T, R> mapErr(Function<X, R> mapper) {
        return new Ok<>(this.value);
    }

    @Override
    public <R> R match(Function<T, R> whenOk, Function<X, R> whenErr) {
        return whenOk.apply(this.value);
    }
}
