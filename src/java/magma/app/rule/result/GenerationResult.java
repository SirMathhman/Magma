package magma.app.rule.result;

import java.util.Optional;
import java.util.function.Function;

public record GenerationResult(Optional<String> value) {
    public GenerationResult flatMap(Function<String, GenerationResult> mapper) {
        return new GenerationResult(this.value.flatMap(inner -> mapper.apply(inner).value));
    }

    public String orElse(String other) {
        return this.value.orElse(other);
    }

    public GenerationResult map(Function<? super String, String> mapper) {
        return new GenerationResult(this.value.map(mapper));
    }
}
