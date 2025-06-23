package magma.app.compile.rule;

import magma.api.list.ListLike;
import magma.app.compile.accumulate.Accumulator;
import magma.app.compile.accumulate.AccumulatorFactory;
import magma.app.compile.factory.ParentNodeResultFactory;
import magma.app.compile.factory.ParentStringResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.result.Matching;

import java.util.function.Function;

public final class OrRule<Node extends DisplayNode, Error, NodeResult extends Matching<Node, Error>, StringResult extends Matching<String, Error>, Errors, Factory extends ParentNodeResultFactory<Node, NodeResult, Errors> & ParentStringResultFactory<Node, StringResult, Errors>> implements
        Rule<Node, NodeResult, StringResult> {
    private final ListLike<Rule<Node, NodeResult, StringResult>> rules;
    private final Factory resultFactory;
    private final AccumulatorFactory<Error, Errors> accumulatorFactory;

    public OrRule(final ListLike<Rule<Node, NodeResult, StringResult>> rules, final Factory resultFactory, final AccumulatorFactory<Error, Errors> accumulatorFactory) {
        this.rules = rules;
        this.resultFactory = resultFactory;
        this.accumulatorFactory = accumulatorFactory;
    }

    @Override
    public NodeResult lex(final String input) {
        return or(rule -> rule.lex(input),
                resultFactory::fromNode,
                errors -> resultFactory.fromNodeErrorWithChildren("Invalid combination", input, errors));
    }

    private <Value, Result extends Matching<Value, Error>, Return> Return or(final Function<Rule<Node, NodeResult, StringResult>, Result> mapper, final Function<Value, Return> whenOk, final Function<Errors, Return> whenError) {

        return rules.stream()
                .<Accumulator<Value, Error, Errors>>reduce(accumulatorFactory.createAccumulator(),
                        (orState, result) -> {
                            if (orState.hasValue())
                                return orState;
                            return mapper.apply(result)
                                    .match(orState::withValue, orState::withError);
                        },
                        (_, next) -> next)
                .match(whenOk, whenError);
    }

    @Override
    public StringResult generate(final Node node) {
        return or(rule -> rule.generate(node),
                resultFactory::fromString,
                errors -> resultFactory.fromStringErrorWithChildren("Invalid combination", node, errors));
    }
}
