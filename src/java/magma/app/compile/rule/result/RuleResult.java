package magma.app.compile.rule.result;

import magma.app.compile.CompileError;
import magma.app.compile.error.context.NodeContext;
import magma.app.compile.error.context.StringContext;
import magma.app.compile.node.DisplayableNode;

public sealed interface RuleResult<Value> permits RuleResult.RuleResultErr, RuleResult.RuleResultOk {
    record RuleResultErr<Value>(CompileError error) implements RuleResult<Value> {
    }

    record RuleResultOk<Value>(Value value) implements RuleResult<Value> {
    }

    static <Value> RuleResult<Value> createFromString(String message, String context) {
        return new RuleResultErr<>(new CompileError(message, new StringContext(context)));
    }

    static <Value> RuleResult<Value> createFromNode(String message, DisplayableNode node) {
        return new RuleResultErr<>(new CompileError(message, new NodeContext(node)));
    }
}