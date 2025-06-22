package magma.rule;

import magma.error.CompileError;
import magma.error.NodeContext;
import magma.error.StringContext;
import magma.list.ListLike;
import magma.node.Node;
import magma.node.result.Matching;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.StringErr;
import magma.string.StringResult;

import java.util.function.Function;

public record OrRule(ListLike<Rule<Node, StringResult>> rules) implements Rule<Node, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        return this.or(rule -> rule.lex(input),
                NodeOk::new,
                errors -> new NodeErr(new CompileError("Invalid combination", new StringContext(input), errors)));
    }

    private <Value, Result extends Matching<Value>, Return> Return or(final Function<Rule<Node, StringResult>, Result> mapper, final Function<Value, Return> whenOk, final Function<ListLike<CompileError>, Return> whenError) {
        return this.rules.stream()
                .map(mapper)
                .<Accumulator<Value>>reduce(new ImmutableAccumulator<Value>(),
                        (orState, result) -> result.match(orState::withValue, orState::withError),
                        (_, next) -> next)
                .match(whenOk, whenError);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.<String, StringResult, StringResult>or(rule -> rule.generate(node),
                StringOk::new,
                errors -> new StringErr(new CompileError("Invalid combination", new NodeContext(node), errors)));
    }
}
