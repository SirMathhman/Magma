package magma.string.result;

import magma.error.CompileError;

import java.util.Optional;
import java.util.function.Function;

public record StringErr(CompileError error) implements StringResult {
    @Override
    public Optional<String> toOptional() {
        return Optional.empty();
    }

    @Override
    public StringResult appendResult(final StringResult other) {
        return this;
    }

    @Override
    public StringResult prepend(final String other) {
        return this;
    }

    @Override
    public StringResult appendSlice(final String slice) {
        return this;
    }

    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<CompileError, Return> whenErr) {
        return whenErr.apply(this.error);
    }
}
