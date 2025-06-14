package magma.app.rule.result;

import java.util.Optional;
import java.util.function.Function;

public record GenerationResult(Optional<String> value) {
    public static GenerationResult of(String value) {
        return new GenerationResult(Optional.of(value));
    }

    public GenerationResult flatMap(Function<String, GenerationResult> mapper) {
        return new GenerationResult(this.value.flatMap(inner -> mapper.apply(inner).value));
    }

    public GenerationResult map(Function<String, String> mapper) {
        return new GenerationResult(this.value.map(mapper));
    }
}
