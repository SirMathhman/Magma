package magma.api.result;

import java.util.function.Function;

public record Err<Value, Error>(Error error) implements Result<Value, Error> {
    @Override
    public <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper) {
        return new Err<>(mapper.apply(this.error));
    }

    @Override
    public <Return> Result<Return, Error> flatMap(Function<Value, Result<Return, Error>> mapper) {
        return new Err<>(this.error);
    }
}
