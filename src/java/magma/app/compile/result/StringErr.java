package magma.app.compile.result;

import magma.api.result.Err;
import magma.api.result.Result;

import java.util.function.Supplier;

public record StringErr(CompileError error) implements StringResult {
    @Override
    public StringResult appendResult(final Supplier<StringResult> other) {
        return this;
    }

    @Override
    public StringResult prependSlice(final String slice) {
        return this;
    }

    @Override
    public Result<String, CompileError> toResult() {
        return new Err<>(this.error);
    }

    @Override
    public StringResult appendSlice(final String slice) {
        return this;
    }
}
