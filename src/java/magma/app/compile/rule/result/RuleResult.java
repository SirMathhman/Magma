package magma.app.compile.rule.result;

import magma.app.compile.CompileError;
import magma.app.compile.error.context.NodeContext;
import magma.app.compile.error.context.StringContext;
import magma.app.compile.node.DisplayableNode;

import java.util.function.Function;

public sealed interface RuleResult<Value> permits RuleResult.Err, RuleResult.Ok {
    record Err<Value>(CompileError error) implements RuleResult<Value> {
    }

    record Ok<Value>(Value value) implements RuleResult<Value> {
    }

    static <Value> RuleResult<Value> createFromString(String message, String context) {
        return new Err<>(new CompileError(message, new StringContext(context)));
    }

    static <Value> RuleResult<Value> createFromValue(Value value) {
        return new Ok<>(value);
    }

    static <Value> RuleResult<Value> createFromNode(String message, DisplayableNode node) {
        return new Err<>(new CompileError(message, new NodeContext(node)));
    }

    default <Return> Return match(Function<Value, Return> whenOk, Function<CompileError, Return> whenErr) {
        return switch (this) {
            case RuleResult.Err<Value>(var error) -> whenErr.apply(error);
            case RuleResult.Ok<Value>(var value) -> whenOk.apply(value);
        };
    }
}