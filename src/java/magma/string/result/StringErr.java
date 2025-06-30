package magma.string.result;

import magma.error.CompileError;

import java.util.Optional;

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
}
