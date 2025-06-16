package magma.app.compile.error;

import magma.api.Err;
import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.function.Function;
import java.util.function.Supplier;

public record StringErr<Error>(Error error) implements StringResult<Error> {
    @Override
    public StringResult<Error> appendResult(Supplier<StringResult<Error>> other) {
        return this;
    }

    @Override
    public StringResult<Error> complete(Function<String, String> mapper) {
        return this;
    }

    @Override
    public StringResult<Error> prependSlice(String slice) {
        return this;
    }

    @Override
    public StringResult<Error> appendSlice(String slice) {
        return this;
    }

    @Override
    public Result<String, Error> toResult() {
        return new Err<>(this.error);
    }

    @Override
    public OrState<String, Error> attachToState(OrState<String, Error> state) {
        return state.withError(this.error);
    }
}
