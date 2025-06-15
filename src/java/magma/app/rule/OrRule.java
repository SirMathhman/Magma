package magma.app.rule;

import magma.app.CompileError;
import magma.app.Context;
import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.Attachable;
import magma.app.maybe.NodeResult;
import magma.app.maybe.StringResult;
import magma.app.maybe.node.ErrNodeResult;
import magma.app.maybe.node.OkNodeResult;
import magma.app.maybe.string.ErrStringResult;
import magma.app.maybe.string.OkStringResult;
import magma.app.rule.or.InlineOrState;
import magma.app.rule.or.OrState;

import java.util.List;
import java.util.function.Function;

public record OrRule(List<Rule<Node, NodeResult, StringResult>> rules) implements Rule<Node, NodeResult, StringResult> {
    @Override
    public StringResult generate(Node node) {
        return this.<Attachable<String>, String, StringResult>or(rule1 -> rule1.generate(node), OkStringResult::new, errors -> new ErrStringResult(this.createError(new NodeContext(node), errors)));
    }

    private CompileError createError(Context context, List<CompileError> errors) {
        return new CompileError("No valid combination", context, errors);
    }

    private <MaybeValue extends Attachable<Value>, Value, Return> Return or(Function<Rule<Node, NodeResult, StringResult>, MaybeValue> mapper, Function<Value, Return> whenPresent, Function<List<CompileError>, Return> whenMissing) {
        final var reduce = this.rules.stream().map(mapper).<OrState<Value>>reduce(new InlineOrState<Value>(), (orState, maybeString) -> {
            if (orState.hasValue())
                return orState;
            return maybeString.attachTo(orState);
        }, (_, next) -> next);
        return reduce.match(whenPresent, whenMissing);
    }

    @Override
    public NodeResult lex(String input) {
        return this.<Attachable<Node>, Node, NodeResult>or(rule1 -> rule1.lex(input), OkNodeResult::new, errors -> {
            return new ErrNodeResult(this.createError(new StringContext(input), errors));
        });
    }
}
