package magma.app.compile.result;

import magma.api.result.Err;
import magma.api.result.Result;

import java.util.function.Supplier;

public record GenerateErr(CompileError error) implements GenerateResult {
    @Override
    public GenerateResult appendResult(final Supplier<GenerateResult> other) {
        return this;
    }

    @Override
    public GenerateResult prependSlice(final String slice) {
        return this;
    }

    @Override
    public Result<String, CompileError> toResult() {
        return new Err<>(this.error);
    }

    @Override
    public GenerateResult appendSlice(final String slice) {
        return this;
    }
}
