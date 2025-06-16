package magma.app.compile.error;

import magma.api.Err;
import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.function.Function;
import java.util.function.Supplier;

public record StringErr(FormattedError error) implements StringResult {
    @Override
    public StringResult appendResult(Supplier<StringResult> other) {
        return this;
    }

    @Override
    public StringResult complete(Function<String, String> mapper) {
        return this;
    }

    @Override
    public StringResult prependSlice(String slice) {
        return this;
    }

    @Override
    public StringResult appendSlice(String slice) {
        return this;
    }

    @Override
    public Result<String, FormattedError> toResult() {
        return new Err<>(this.error);
    }

    @Override
    public OrState<String> attachToState(OrState<String> state) {
        return state.withError(this.error);
    }
}
