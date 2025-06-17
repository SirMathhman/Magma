package magma.api.result;

import java.util.function.Function;

public record Err<Value, Error>(Error error) implements Result<Value, Error> {
    @Override
    public <Return> Return match(Function<Value, Return> whenOk, Function<Error, Return> whenErr) {
        return whenErr.apply(this.error);
    }

    @Override
    public <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper) {
        return new Err<>(mapper.apply(this.error));
    }
}
