package magma.app.compile.result;

import magma.api.result.Ok;
import magma.api.result.Result;

import java.util.function.Supplier;

public record StringOk(String value) implements StringResult {
    @Override
    public StringResult appendResult(final Supplier<StringResult> other) {
        return other.get()
                .prependSlice(this.value);
    }

    @Override
    public StringResult prependSlice(final String slice) {
        return new StringOk(slice + this.value);
    }

    @Override
    public Result<String, CompileError> toResult() {
        return new Ok<>(this.value);
    }

    @Override
    public StringResult appendSlice(final String slice) {
        return new StringOk(this.value + slice);
    }
}
