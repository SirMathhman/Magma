package magma.rule;

import magma.string.result.StringResult;

import java.util.Optional;

public record StringOk(String value) implements StringResult {
    @Override
    public Optional<String> toOptional() {
        return Optional.of(this.value);
    }
}
