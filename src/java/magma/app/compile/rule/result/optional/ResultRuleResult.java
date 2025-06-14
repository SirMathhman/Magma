package magma.app.compile.rule.result.optional;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.error.Context;
import magma.app.compile.error.context.NodeContext;
import magma.app.compile.error.context.StringContext;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.result.RuleResult;

import java.util.Optional;
import java.util.function.Function;

public record ResultRuleResult<Value>(Result<Value, CompileError> maybeValue) implements RuleResult<Value> {
    public static <N> RuleResult<N> createFromString(String message, String context) {
        return createFromErrorWithContext(message, new StringContext(context));
    }

    private static <Value> RuleResult<Value> createFromErrorWithContext(String message, Context context) {
        return new ResultRuleResult<>(new Err<>(new CompileError(message, context)));
    }

    public static <Value> RuleResult<Value> createFromValue(Value value) {
        return new ResultRuleResult<>(new Ok<>(value));
    }

    public static <Value> RuleResult<Value> createFromNode(String message, CompoundNode node) {
        return new ResultRuleResult<>(new Err<>(new CompileError(message, new NodeContext(node))));
    }

    @Override
    public RuleResult<Value> flatMap(Function<Value, RuleResult<Value>> mapper) {
        return new ResultRuleResult<>(this.maybeValue.flatMapValue(value -> mapper.apply(value).findAsResult()));
    }

    @Override
    public RuleResult<Value> map(Function<Value, Value> mapper) {
        return new ResultRuleResult<>(this.maybeValue.mapValue(mapper));
    }

    @Override
    public Optional<Value> findAsOption() {
        return this.maybeValue.findValue();
    }

    @Override
    public Result<Value, CompileError> findAsResult() {
        return this.maybeValue;
    }
}
