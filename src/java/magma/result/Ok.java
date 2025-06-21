package magma.result;

import java.util.function.Function;

public record Ok<Value, Error>(Value value) implements Result<Value, Error> {
    @Override
    public <Return> Result<Return, Error> flatMapValue(final Function<Value, Result<Return, Error>> mapper) {
        return mapper.apply(this.value);
    }

    @Override
    public <Return> Result<Return, Error> mapValue(final Function<Value, Return> mapper) {
        return new Ok<>(mapper.apply(this.value));
    }

    @Override
    public <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenErr) {
        return whenOk.apply(this.value);
    }
}
