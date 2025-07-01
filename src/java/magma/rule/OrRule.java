package magma.rule;

import magma.compile.result.ResultFactory;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.result.Matchable;
import magma.rule.accumulate.Accumulator;
import magma.rule.accumulate.MutableAccumulator;
import magma.string.result.StringResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class OrRule<Node, Error> implements Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> {
    private final List<Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>>> rules;
    private final ResultFactory<Node, Error, StringResult<Error>> factory;

    public OrRule(final List<Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>>> rules,
                  final ResultFactory<Node, Error, StringResult<Error>> factory) {
        this.rules = new ArrayList<>(rules);
        this.factory = factory;
    }

    private static <Value, Error, Result extends Matchable<Value, Error>> Accumulator<Value, Error> fold(final Accumulator<Value, Error> accumulator,
                                                                                                         final Result result) {
        if (accumulator.isPresent()) return accumulator;
        return result.match(accumulator::withValue, accumulator::withError);
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> lex(final String input) {
        return this.or(rule -> rule.lex(input))
                   .match(NodeOk::new,
                          errors -> this.factory.createNodeErrorWithChildren("No valid combination present", input,
                                                                             errors));
    }

    private <Value, Result extends Matchable<Value, Error>> Accumulator<Value, Error> or(final Function<Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>>, Result> mapper) {
        return this.rules.stream()
                         .map(mapper)
                         .<Accumulator<Value, Error>>reduce(new MutableAccumulator<>(), OrRule::fold,
                                                            (_, next) -> next);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.or(rule -> rule.generate(node))
                   .match(StringOk::new,
                          errors -> this.factory.createStringErrorWithChildren("No valid combination present", node,
                                                                               errors));
    }
}
