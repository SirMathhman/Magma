package magma.api.result;

import java.util.function.Function;

public record Err<T, X>(X error) implements Result<T, X> {
    @Override
    public <Result> Result match(Function<T, Result> whenOk, Function<X, Result> whenErr) {
        return whenErr.apply(this.error);
    }
}
