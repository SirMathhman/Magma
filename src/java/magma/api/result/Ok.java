package magma.api.result;

import java.util.function.Function;

public record Ok<Value, Error>(Value value) implements Result<Value, Error> {
    @Override
    public <Return> Result<Value, Return> mapErr(Function<Error, Return> mapper) {
        return new Ok<>(this.value);
    }

}
