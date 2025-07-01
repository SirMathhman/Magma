package magma.rule;

import magma.compile.result.NodeResultFactory;
import magma.compile.result.StringResultFactory;
import magma.result.Matchable;
import magma.rule.accumulate.Accumulator;
import magma.rule.accumulate.MutableAccumulator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class OrRule<Node, Error, StringResult extends Matchable<String, Error>, NodeResult extends Matchable<Node, Error>, ResultFactory extends NodeResultFactory<Node, Error, NodeResult> & StringResultFactory<Node, Error, StringResult>>
        implements Rule<Node, NodeResult, StringResult> {
    private final List<Rule<Node, NodeResult, StringResult>> rules;
    private final ResultFactory factory;

    public OrRule(final List<Rule<Node, NodeResult, StringResult>> rules, final ResultFactory factory) {
        this.rules = new ArrayList<>(rules);
        this.factory = factory;
    }

    private static <Value, Error, Result extends Matchable<Value, Error>> Accumulator<Value, Error> fold(final Accumulator<Value, Error> accumulator,
                                                                                                         final Result result) {
        if (accumulator.isPresent()) return accumulator;
        return result.match(accumulator::withValue, accumulator::withError);
    }

    @Override
    public NodeResult lex(final String input) {
        return this.or(rule -> rule.lex(input))
                   .match(this.factory::createNode,
                          errors -> this.factory.createNodeErrorWithChildren("No valid combination present", input,
                                                                             errors));
    }

    private <Value, Result extends Matchable<Value, Error>> Accumulator<Value, Error> or(final Function<Rule<Node, NodeResult, StringResult>, Result> mapper) {
        return this.rules.stream()
                         .map(mapper)
                         .<Accumulator<Value, Error>>reduce(new MutableAccumulator<>(), OrRule::fold,
                                                            (_, next) -> next);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.or(rule -> rule.generate(node))
                   .match(this.factory::createString,
                          errors -> this.factory.createStringErrorWithChildren("No valid combination present", node,
                                                                               errors));
    }
}
