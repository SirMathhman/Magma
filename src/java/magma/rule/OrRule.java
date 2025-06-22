package magma.rule;

import magma.error.ErrorSequence;
import magma.factory.ResultFactory;
import magma.list.ListLike;
import magma.node.DisplayNode;
import magma.node.result.Matching;
import magma.node.result.NodeResult;
import magma.string.StringResult;

import java.util.function.Function;

public final class OrRule<Node extends DisplayNode, Error> implements Rule<Node, NodeResult<Node, Error>, StringResult<Error>> {
    private final ListLike<Rule<Node, NodeResult<Node, Error>, StringResult<Error>>> rules;
    private final ResultFactory<Node, NodeResult<Node, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory;

    public OrRule(final ListLike<Rule<Node, NodeResult<Node, Error>, StringResult<Error>>> rules, final ResultFactory<Node, NodeResult<Node, Error>, StringResult<Error>, ErrorSequence<Error>> resultFactory) {
        this.rules = rules;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node, Error> lex(final String input) {
        return this.or(rule -> rule.lex(input),
                this.resultFactory::fromNode,
                errors -> this.resultFactory.fromNodeErrorWithChildren("Invalid combination", input, errors));
    }

    private <Value, Result extends Matching<Value, Error>, Return> Return or(final Function<Rule<Node, NodeResult<Node, Error>, StringResult<Error>>, Result> mapper, final Function<Value, Return> whenOk, final Function<ErrorSequence<Error>, Return> whenError) {
        
        return this.rules.stream()
                .<Accumulator<Value, Error>>reduce(new ImmutableAccumulator<>(), (orState, result) -> {
                    if (orState.hasValue())
                        return orState;
                    return mapper.apply(result)
                            .match(orState::withValue, orState::withError);
                }, (_, next) -> next)
                .match(whenOk, whenError);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.or(rule -> rule.generate(node),
                this.resultFactory::fromString,
                errors -> this.resultFactory.fromStringErrorWithChildren("Invalid combination", node, errors));
    }
}
