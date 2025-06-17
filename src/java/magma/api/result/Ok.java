package magma.api.result;

import java.util.function.Function;

public record Ok<T, X>(T value) implements Result<T, X> {
    @Override
    public <Result> Result match(Function<T, Result> whenOk, Function<X, Result> whenErr) {
        return whenOk.apply(this.value);
    }
}
