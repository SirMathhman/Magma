package magma.api.result;

import java.util.function.Function;

public record Err<T, X>(X error) implements Result<T, X> {
    @Override
    public <Return> Result<Return, X> map(Function<T, Return> mapper) {
        return new Err<>(this.error);
    }
}
