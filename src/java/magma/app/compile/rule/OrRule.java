package magma.app.compile.rule;

import magma.api.collect.seq.Sequence;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.rule.action.CompileResults;

import java.util.Optional;

public record OrRule<Node>(
        Sequence<Rule<Node, NodeResult<Node>, StringResult>> rules) implements Rule<Node, NodeResult<Node>, StringResult> {
    private Optional<String> generate0(Node node) {
        for (var i = 0; i < this.rules.size(); i++) {
            final var rule = this.rules.get(i);
            final var maybeGenerated = rule.generate(node)
                    .findValue();
            if (maybeGenerated.isPresent())
                return maybeGenerated;
        }

        return Optional.empty();
    }

    private Optional<Node> lex0(String input) {
        for (var i = 0; i < this.rules.size(); i++) {
            final var rule = this.rules.get(i);
            final var maybeLex = rule.lex(input)
                    .findValue();
            if (maybeLex.isPresent())
                return maybeLex;
        }

        return Optional.empty();
    }

    @Override
    public NodeResult<Node> lex(String input) {
        Optional<Node> option = this.lex0(input);
        return option.map(CompileResults::fromNodeValue)
                .orElseGet(() -> CompileResults.fromNodeError(input, "Invalid value"));
    }

    @Override
    public StringResult generate(Node node) {
        Optional<String> option = this.generate0(node);
        return option.map(CompileResults::fromStringValue)
                .orElseGet(() -> CompileResults.fromStringError(node));
    }
}
