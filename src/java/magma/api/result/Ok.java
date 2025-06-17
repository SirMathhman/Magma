package magma.api.result;

import java.util.function.Function;

public record Ok<T, X>(T value) implements Result<T, X> {
    @Override
    public <Return> Return match(Function<T, Return> whenOk, Function<X, Return> whenErr) {
        return whenOk.apply(this.value);
    }

    @Override
    public <Return> Result<T, Return> mapErr(Function<X, Return> mapper) {
        return new Ok<>(this.value);
    }
}
