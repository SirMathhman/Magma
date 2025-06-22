package magma.rule;

import magma.error.FormattedError;
import magma.factory.ResultFactory;
import magma.list.ListLike;
import magma.node.DisplayNode;
import magma.node.result.Matching;
import magma.node.result.NodeResult;
import magma.string.StringResult;

import java.util.function.Function;

public final class OrRule<Node extends DisplayNode> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final ListLike<Rule<Node, NodeResult<Node>, StringResult>> rules;
    private final ResultFactory<Node, NodeResult<Node>, StringResult> resultFactory;

    public OrRule(final ListLike<Rule<Node, NodeResult<Node>, StringResult>> rules, final ResultFactory<Node, NodeResult<Node>, StringResult> resultFactory) {
        this.rules = rules;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        return this.or(rule -> rule.lex(input),
                this.resultFactory::fromNode,
                errors -> this.resultFactory.fromNodeErrorWithChildren("Invalid combination", input, errors));
    }

    private <Value, Result extends Matching<Value>, Return> Return or(final Function<Rule<Node, NodeResult<Node>, StringResult>, Result> mapper, final Function<Value, Return> whenOk, final Function<ListLike<FormattedError>, Return> whenError) {
        return this.rules.stream()
                .<Accumulator<Value>>reduce(new ImmutableAccumulator<>(), (orState, result) -> {
                    if (orState.hasValue())
                        return orState;
                    return mapper.apply(result)
                            .match(orState::withValue, orState::withError);
                }, (_, next) -> next)
                .match(whenOk, whenError);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.or(rule -> rule.generate(node),
                this.resultFactory::fromString,
                errors -> this.resultFactory.fromStringErrorWithChildren("Invalid combination", node, errors));
    }
}
