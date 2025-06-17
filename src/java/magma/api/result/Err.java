package magma.api.result;

import java.util.function.Function;

public record Err<T, X>(X error) implements Result<T, X> {
    @Override
    public <Return> Result<Return, X> map(Function<T, Return> mapper) {
        return new Err<Return, X>(this.error);
    }

    @Override
    public <Return> Result<Return, X> flatMap(Function<T, Result<Return, X>> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public <Return> Return match(Function<T, Return> whenOk, Function<X, Return> whenError) {
        return whenError.apply(this.error);
    }
}
