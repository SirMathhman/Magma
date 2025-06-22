package magma.string;

import magma.error.CompileError;
import magma.result.Err;
import magma.result.Result;

import java.util.function.Supplier;

public record StringErr(CompileError error) implements StringResult {
    @Override
    public StringResult appendSlice(final String slice) {
        return this;
    }

    @Override
    public Result<String, CompileError> toResult() {
        return new Err<>(this.error);
    }

    @Override
    public StringResult prepend(final String slice) {
        return this;
    }

    @Override
    public StringResult appendResult(final Supplier<StringResult> other) {
        return this;
    }
}
