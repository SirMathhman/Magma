package magma.rule;

import magma.string.result.StringResult;

import java.util.Optional;

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
}
