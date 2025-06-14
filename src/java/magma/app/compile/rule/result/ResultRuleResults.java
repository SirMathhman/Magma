package magma.app.compile.rule.result;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.app.compile.CompileError;
import magma.app.compile.error.Context;
import magma.app.compile.error.context.NodeContext;
import magma.app.compile.error.context.StringContext;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.result.optional.ResultRuleResult;

public class ResultRuleResults {
    public static <Value> RuleResult<Value> createFromString(String message, String context) {
        return createFromErrorWithContext(message, new StringContext(context));
    }

    public static <Value> RuleResult<Value> createFromErrorWithContext(String message, Context context) {
        return new ResultRuleResult<>(new Err<>(new CompileError(message, context)));
    }

    public static <Value> RuleResult<Value> createFromValue(Value value) {
        return new ResultRuleResult<>(new Ok<>(value));
    }

    public static <Value> RuleResult<Value> createFromNode(String message, CompoundNode node) {
        return new ResultRuleResult<>(new Err<>(new CompileError(message, new NodeContext(node))));
    }
}
