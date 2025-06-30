package magma.rule;

import magma.error.CompileError;
import magma.string.result.StringResult;

import java.util.Optional;
import java.util.function.Function;

public record StringOk(String value) implements StringResult {
    public StringOk() {
        this("");
    }

    @Override
    public Optional<String> toOptional() {
        return Optional.of(this.value);
    }

    @Override
    public StringResult appendResult(final StringResult other) {
        return other.prepend(this.value);
    }

    @Override
    public StringResult prepend(final String other) {
        return new StringOk(other + this.value);
    }

    @Override
    public StringResult appendSlice(final String slice) {
        return new StringOk(this.value + slice);
    }

    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<CompileError, Return> whenErr) {
        return whenOk.apply(this.value);
    }
}
