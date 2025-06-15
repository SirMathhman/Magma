package magma.app.compile.rule.result;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileError;
import magma.app.compile.error.Context;
import magma.app.compile.error.context.NodeContext;
import magma.app.compile.error.context.StringContext;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.rule.result.optional.ResultRuleResult;

import java.util.function.Function;

public interface RuleResult<Value> {
    static <Value> RuleResult<Value> createFromString(String message, String context) {
        return createFromErrorWithContext(message, new StringContext(context));
    }

    static <Value> RuleResult<Value> createFromErrorWithContext(String message, Context context) {
        return new ResultRuleResult<>(new Err<>(new CompileError(message, context)));
    }

    static <Value> RuleResult<Value> createFromValue(Value value) {
        return new ResultRuleResult<>(new Ok<>(value));
    }

    static <Value> RuleResult<Value> createFromNode(String message, DisplayableNode node) {
        return new ResultRuleResult<>(new Err<>(new CompileError(message, new NodeContext(node))));
    }

    <Return> RuleResult<Return> flatMap(Function<Value, RuleResult<Return>> mapper);

    <Return> RuleResult<Return> mapValue(Function<Value, Return> mapper);

    Result<Value, CompileError> unwrap();

    <Return> Return match(Function<Value, Return> whenOk, Function<CompileError, Return> whenErr);
}