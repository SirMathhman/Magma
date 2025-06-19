package magma.app.compile.rule;

import magma.api.Result;
import magma.api.collect.seq.Sequence;
import magma.app.compile.rule.action.CompileError;
import magma.app.compile.rule.action.CompileResults;

import java.util.Optional;

public record OrRule<Node>(Sequence<Rule<Node>> rules) implements Rule<Node> {
    @Override
    public Optional<String> generate(Node node) {
        for (var i = 0; i < this.rules.size(); i++) {
            final var rule = this.rules.get(i);
            final var maybeGenerated = rule.generate(node);
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
    public Result<Node, CompileError> lex(String input) {
        return CompileResults.fromOption(this.lex0(input), input);
    }
}
