package magma.string;

import magma.error.CompileError;
import magma.result.Err;
import magma.result.Result;

import java.util.function.Supplier;

public record StringErr() implements StringResult {
    @Override
    public StringResult appendSlice(final String slice) {
        return new StringErr();
    }

    @Override
    public Result<String, CompileError> toResult() {
        return new Err<>(new CompileError());
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
