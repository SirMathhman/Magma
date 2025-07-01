package magma.rule;

import magma.compile.result.ResultFactory;
import magma.error.FormatError;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.result.Matchable;
import magma.rule.accumulate.Accumulator;
import magma.rule.accumulate.MutableAccumulator;
import magma.string.result.StringResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class OrRule<Node> implements Rule<Node, NodeResult<Node>, StringResult<FormatError>> {
    private final List<Rule<Node, NodeResult<Node>, StringResult<FormatError>>> rules;
    private final ResultFactory<Node, StringResult<FormatError>> factory;

    public OrRule(final List<Rule<Node, NodeResult<Node>, StringResult<FormatError>>> rules,
                  final ResultFactory<Node, StringResult<FormatError>> factory) {
        this.rules = new ArrayList<>(rules);
        this.factory = factory;
    }

    private static <Value, Result extends Matchable<Value, FormatError>> Accumulator<Value> fold(final Accumulator<Value> accumulator,
                                                                                                 final Result result) {
        if (accumulator.isPresent()) return accumulator;
        return result.match(accumulator::withValue, accumulator::withError);
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        return this.or(rule -> rule.lex(input))
                   .match(NodeOk::new,
                          errors -> this.factory.createNodeErrorWithChildren("No valid combination present", input,
                                                                             errors));
    }

    private <Value, Result extends Matchable<Value, FormatError>> Accumulator<Value> or(final Function<Rule<Node, NodeResult<Node>, StringResult<FormatError>>, Result> mapper) {
        return this.rules.stream()
                         .map(mapper)
                         .<Accumulator<Value>>reduce(new MutableAccumulator<>(), OrRule::fold, (_, next) -> next);
    }

    @Override
    public StringResult<FormatError> generate(final Node node) {
        return this.or(rule -> rule.generate(node))
                   .match(StringOk::new,
                          errors -> this.factory.createStringErrorWithChildren("No valid combination present", node,
                                                                               errors));
    }
}
