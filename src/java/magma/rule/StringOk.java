package magma.rule;

import magma.error.CompileError;
import magma.result.Ok;
import magma.result.Result;
import magma.string.StringResult;

import java.util.function.Supplier;

public record StringOk(String value) implements StringResult {
    public StringOk() {
        this("");
    }

    @Override
    public StringResult appendSlice(final String slice) {
        return new StringOk(this.value + slice);
    }

    @Override
    public Result<String, CompileError> toResult() {
        return new Ok<>(this.value);
    }

    @Override
    public StringResult prepend(final String slice) {
        return new StringOk(slice + this.value);
    }

    @Override
    public StringResult appendResult(final Supplier<StringResult> other) {
        return other.get()
                .prepend(this.value);
    }
}
