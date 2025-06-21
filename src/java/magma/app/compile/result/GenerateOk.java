package magma.app.compile.result;

import magma.api.result.Ok;
import magma.api.result.Result;

import java.util.function.Supplier;

public record GenerateOk(String value) implements GenerateResult {
    @Override
    public GenerateResult appendResult(final Supplier<GenerateResult> other) {
        return other.get()
                .prependSlice(this.value);
    }

    @Override
    public GenerateResult prependSlice(final String slice) {
        return new GenerateOk(slice + this.value);
    }

    @Override
    public Result<String, CompileError> toResult() {
        return new Ok<>(this.value);
    }

    @Override
    public GenerateResult appendSlice(final String slice) {
        return new GenerateOk(this.value + slice);
    }
}
