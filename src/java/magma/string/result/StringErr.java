package magma.string.result;

import magma.error.FormatError;

import java.util.Optional;
import java.util.function.Function;

public record StringErr(FormatError error) implements StringResult {
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
    public <Return> Return match(final Function<String, Return> whenOk, final Function<FormatError, Return> whenErr) {
        return whenErr.apply(this.error);
    }
}
