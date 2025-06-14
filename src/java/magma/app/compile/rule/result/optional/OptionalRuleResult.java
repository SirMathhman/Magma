package magma.app.compile.rule.result.optional;

import magma.app.compile.rule.result.RuleResult;

import java.util.Optional;
import java.util.function.Function;

public record OptionalRuleResult<T>(Optional<T> maybeValue) implements RuleResult<T> {
    public static <N> RuleResult<N> createEmpty() {
        return new OptionalRuleResult<>(Optional.empty());
    }

    public static <N> RuleResult<N> of(N value) {
        return new OptionalRuleResult<>(Optional.of(value));
    }

    @Override
    public RuleResult<T> flatMap(Function<T, RuleResult<T>> mapper) {
        return new OptionalRuleResult<T>(this.maybeValue.flatMap(mapNode -> mapper.apply(mapNode).findValue()));
    }

    @Override
    public <R> RuleResult<R> map(Function<T, R> mapper) {
        return new OptionalRuleResult<R>(this.maybeValue.map(mapper));
    }

    @Override
    public Optional<T> findValue() {
        return this.maybeValue;
    }
}
