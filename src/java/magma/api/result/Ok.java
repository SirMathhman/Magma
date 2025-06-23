package magma.api.result;

import java.util.function.Function;

public record Ok<Value, Error>(Value value) implements Result<Value, Error> {
    @Override
    public <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenError) {
        return whenOk.apply(value);
    }

    @Override
    public <Return> Result<Return, Error> mapValue(final Function<Value, Return> mapper) {
        return new Ok<>(mapper.apply(value));
    }

    @Override
    public <Return> Result<Return, Error> flatMapValue(final Function<Value, Result<Return, Error>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public <Return> Result<Value, Return> mapErr(final Function<Error, Return> mapper) {
        return new Ok<>(value);
    }
}
