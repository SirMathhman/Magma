package magma.rule;

import magma.error.CompileError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record OrRule(List<Rule<EverythingNode, NodeResult<EverythingNode>, StringResult>> rules)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private record Accumulator(Optional<EverythingNode> maybeValue, List<CompileError> errors) {
        public Accumulator() {
            this(Optional.empty(), new ArrayList<>());
        }

        public boolean isPresent() {
            return this.maybeValue.isPresent();
        }

        public Accumulator withValue(final EverythingNode value) {
            return new Accumulator(Optional.of(value), this.errors);
        }

        public Accumulator withError(final CompileError error) {
            this.errors.add(error);
            return this;
        }

        public <Return> Return match(final Function<EverythingNode, Return> whenOk,
                                     final Function<List<CompileError>, Return> whenErr) {
            return this.maybeValue.map(whenOk::apply).orElseGet(() -> whenErr.apply(this.errors));
        }
    }

    private Optional<String> generate0(final EverythingNode node) {
        return this.rules.stream().map(rule -> rule.generate(node).toOptional()).flatMap(Optional::stream).findFirst();
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.rules.stream()
                         .map(rule -> rule.lex(input))
                         .reduce(new Accumulator(), (accumulator, result) -> {
                             if (accumulator.isPresent()) return accumulator;
                             return result.match(accumulator::withValue, accumulator::withError);
                         }, (_, next) -> next)
                         .<NodeResult<EverythingNode>>match(NodeOk::new, errors -> new NodeErr<>(
                                 new CompileError("No valid combination present", "?", errors)));
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.generate0(node)
                   .<StringResult>map(StringOk::new)
                   .orElseGet(() -> new StringErr(new CompileError(this.getClass().getName(), "?")));
    }
}
