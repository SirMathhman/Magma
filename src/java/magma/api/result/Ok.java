package magma.api.result;

import java.util.function.Function;

public record Ok<T, X>(T value) implements Result<T, X> {
    @Override
    public <Return> Result<Return, X> map(Function<T, Return> mapper) {
        return new Ok<>(mapper.apply(this.value));
    }
}
