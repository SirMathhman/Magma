package magma.app.compile;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.rule.or.Accumulator;

import java.util.function.Supplier;

public record StringErr(FormattedError error) implements StringResult<FormattedError> {
    @Override
    public StringResult<FormattedError> appendResult(Supplier<StringResult<FormattedError>> generate) {
        return new StringErr(this.error());
    }

    @Override
    public StringResult<FormattedError> prependSlice(String slice) {
        return new StringErr(this.error());
    }

    @Override
    public StringResult<FormattedError> appendSlice(String infix) {
        return this;
    }

    @Override
    public Accumulator<String, FormattedError> attachToState(Accumulator<String, FormattedError> state) {
        return state.withError(this.error());
    }

    @Override
    public Result<String, FormattedError> toResult() {
        return new Err<>(this.error);
    }
}
