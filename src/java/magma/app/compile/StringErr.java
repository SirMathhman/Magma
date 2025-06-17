package magma.app.compile;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.rule.or.Accumulator;

import java.util.function.Supplier;

public record StringErr<Error>(Error error) implements StringResult<Error, Result<String, Error>> {
    @Override
    public StringResult<Error, Result<String, Error>> appendResult(Supplier<StringResult<Error, Result<String, Error>>> generate) {
        return new StringErr<Error>(this.error());
    }

    @Override
    public StringResult<Error, Result<String, Error>> prependSlice(String slice) {
        return new StringErr<>(this.error());
    }

    @Override
    public StringResult<Error, Result<String, Error>> appendSlice(String infix) {
        return this;
    }

    @Override
    public Accumulator<String, Error> attachToState(Accumulator<String, Error> state) {
        return state.withError(this.error());
    }

    @Override
    public Result<String, Error> toResult() {
        return new Err<>(this.error);
    }
}
