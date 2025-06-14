package magma.app.compile.rule.result.optional;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.error.context.StringContext;
import magma.app.compile.rule.result.RuleResult;

import java.util.Optional;
import java.util.function.Function;

public record OptionalRuleResult<Node>(Result<Node, CompileError> maybeValue) implements RuleResult<Node> {
    public static <N> RuleResult<N> createEmpty() {
        return new OptionalRuleResult<>(new Err<>(new CompileError("", new StringContext(""))));
    }

    public static <N> RuleResult<N> createFrom(N value) {
        return new OptionalRuleResult<>(new Ok<>(value));
    }

    @Override
    public RuleResult<Node> flatMap(Function<Node, RuleResult<Node>> mapper) {
        return new OptionalRuleResult<>(this.maybeValue.flatMapValue(mapNode -> mapper.apply(mapNode).findAsResult()));
    }

    @Override
    public RuleResult<Node> map(Function<Node, Node> mapper) {
        return new OptionalRuleResult<>(this.maybeValue.mapValue(mapper));
    }

    @Override
    public Optional<Node> findAsOption() {
        return this.maybeValue.findValue();
    }

    @Override
    public Result<Node, CompileError> findAsResult() {
        return this.maybeValue;
    }
}
