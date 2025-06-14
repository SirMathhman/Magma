package magma.app.compile.rule.result.optional;

import magma.app.compile.rule.result.RuleResult;

import java.util.Optional;
import java.util.function.Function;

public record OptionalRuleResult<N>(Optional<N> maybeValue) implements RuleResult<N> {
    public static <N> RuleResult<N> createEmpty() {
        return new OptionalRuleResult<>(Optional.empty());
    }

    public static <N> RuleResult<N> of(N value) {
        return new OptionalRuleResult<>(Optional.of(value));
    }

    @Override
    public RuleResult<N> flatMap(Function<N, RuleResult<N>> mapper) {
        return new OptionalRuleResult<>(this.maybeValue.flatMap(mapNode -> mapper.apply(mapNode).findValue()));
    }

    @Override
    public RuleResult<N> map(Function<N, N> mapper) {
        return new OptionalRuleResult<>(this.maybeValue.map(mapper));
    }

    @Override
    public Optional<N> findValue() {
        return this.maybeValue;
    }
}
