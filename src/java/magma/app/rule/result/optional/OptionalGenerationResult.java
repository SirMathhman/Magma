package magma.app.rule.result.optional;

import magma.app.rule.result.GenerationResult;

import java.util.Optional;
import java.util.function.Function;

public record OptionalGenerationResult(Optional<String> value) implements GenerationResult {
    public static OptionalGenerationResult of(String value) {
        return new OptionalGenerationResult(Optional.of(value));
    }

    @Override
    public GenerationResult flatMap(Function<String, GenerationResult> mapper) {
        return new OptionalGenerationResult(this.value.flatMap(inner -> mapper.apply(inner).findValue()));
    }

    @Override
    public GenerationResult map(Function<String, String> mapper) {
        return new OptionalGenerationResult(this.value.map(mapper));
    }

    @Override
    public Optional<String> findValue() {
        return this.value;
    }
}
