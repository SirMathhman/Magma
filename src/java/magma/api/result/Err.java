package magma.api.result;

import java.util.function.Function;

public record Err<Value, Error>(Error error) implements Result<Value, Error> {
    @Override
    public <Return> Return match(final Function<Value, Return> whenOk, final Function<Error, Return> whenError) {
        return whenError.apply(error);
    }

    @Override
    public <Return> Result<Return, Error> mapValue(final Function<Value, Return> mapper) {
        return new Err<>(error);
    }

    @Override
    public <Return> Result<Return, Error> flatMapValue(final Function<Value, Result<Return, Error>> mapper) {
        return new Err<>(error);
    }

    @Override
    public <Return> Result<Value, Return> mapErr(final Function<Error, Return> mapper) {
        return new Err<>(mapper.apply(error));
    }
}
