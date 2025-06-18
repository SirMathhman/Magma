package magma.app.compile.rule;

import magma.api.list.Sequence;

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

    @Override
    public Optional<Node> lex(String input) {
        for (var i = 0; i < this.rules.size(); i++) {
            final var rule = this.rules.get(i);
            final var maybeLex = rule.lex(input);
            if (maybeLex.isPresent())
                return maybeLex;
        }

        return Optional.empty();
    }
}
