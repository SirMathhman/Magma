package magma.rule;

import magma.error.CompileError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.result.Matchable;
import magma.rule.accumulate.Accumulator;
import magma.rule.accumulate.MutableAccumulator;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.List;
import java.util.function.Function;

public record OrRule(List<Rule<EverythingNode, NodeResult<EverythingNode>, StringResult>> rules)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.or(rule -> rule.lex(input))
                   .<NodeResult<EverythingNode>>match(NodeOk::new, errors -> new NodeErr<>(
                           new CompileError("No valid combination present", input, errors)));
    }

    private <Value, Result extends Matchable<Value>> Accumulator<Value> or(final Function<Rule<EverythingNode, NodeResult<EverythingNode>, StringResult>, Result> mapper) {
        return this.rules.stream().map(mapper).< Accumulator<Value> >reduce(new MutableAccumulator<>(), (accumulator, result) -> {
            if (accumulator.isPresent()) return accumulator;
            return result.match(accumulator::withValue, accumulator::withError);
        }, (_, next) -> next);
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.or(rule -> rule.generate(node))
                   .<StringResult>match(StringOk::new, errors -> new StringErr(
                           new CompileError("No valid combination present", node.toString(), errors)));
    }
}
