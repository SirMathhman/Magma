package magma.app.rule;

import java.util.Optional;
import java.util.function.Function;

public record RuleResult(Optional<String> value) {
    public RuleResult flatMap(Function<String, RuleResult> mapper) {
        return new RuleResult(this.value.flatMap(inner -> mapper.apply(inner).value));
    }

    public String orElse(String other) {
        return this.value.orElse(other);
    }

    public RuleResult map(Function<? super String, String> mapper) {
        return new RuleResult(this.value.map(mapper));
    }
}
