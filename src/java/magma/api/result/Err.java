package magma.api.result;

import java.util.function.Function;

public record Err<T, X>(X error) implements Result<T, X> {
    @Override
    public <Return> Return match(Function<T, Return> whenOk, Function<X, Return> whenErr) {
        return whenErr.apply(this.error);
    }
}
