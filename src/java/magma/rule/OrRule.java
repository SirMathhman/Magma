package magma.rule;

import magma.error.CompileError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.result.Matchable;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record OrRule(List<Rule<EverythingNode, NodeResult<EverythingNode>, StringResult>> rules)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private static final class Accumulator<Node> {
        private final Optional<Node> maybeValue;
        private final List<CompileError> errors;

        private Accumulator(final Optional<Node> maybeValue, final List<CompileError> errors) {
            this.maybeValue = maybeValue;
            this.errors = errors;
        }

        public Accumulator() {
            this(Optional.empty(), new ArrayList<>());
        }

        public boolean isPresent() {
            return this.maybeValue.isPresent();
        }

        public Accumulator<Node> withValue(final Node value) {
            return new Accumulator<Node>(Optional.of(value), this.errors);
        }

        public Accumulator<Node> withError(final CompileError error) {
            this.errors.add(error);
            return this;
        }

        public <Return> Return match(final Function<Node, Return> whenOk,
                                     final Function<List<CompileError>, Return> whenErr) {
            return this.maybeValue.map(whenOk::apply).orElseGet(() -> whenErr.apply(this.errors));
        }
    }

    private Optional<String> generate0(final EverythingNode node) {
        return this.rules.stream().map(rule -> rule.generate(node).toOptional()).flatMap(Optional::stream).findFirst();
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.or(rule -> rule.lex(input))
                   .<NodeResult<EverythingNode>>match(NodeOk::new, errors -> new NodeErr<>(
                           new CompileError("No valid combination present", input, errors)));
    }

    private <Value, Result extends Matchable<Value>> Accumulator<Value> or(final Function<Rule<EverythingNode, NodeResult<EverythingNode>, StringResult>, Result> mapper) {
        return this.rules.stream().map(mapper).reduce(new Accumulator<>(), (accumulator, result) -> {
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
