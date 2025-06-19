package magma.app.compile.rule;

import magma.api.Result;
import magma.api.collect.seq.Sequence;
import magma.app.compile.error.NodeResult;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.CompileResults;

import java.util.Optional;

public record OrRule<Node>(
        Sequence<Rule<Node, NodeResult<Node>, Result<String, CompileError>>> rules) implements Rule<Node, NodeResult<Node>, Result<String, CompileError>> {
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
        return CompileResults.fromOptionWithString(this.lex0(input), input);
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return CompileResults.fromOptionWithNode(this.generate0(node), node);
    }
}
