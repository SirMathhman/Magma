package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.api.collect.seq.Sequence;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.StringContext;

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
        return this.lex0(input)
                .<Result<Node, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid input", new StringContext(input))));
    }
}
