package magma.app;

import magma.app.node.Node;

import java.util.Optional;

public record TypeRule(String type, Rule rule) implements Rule {
    @Override
    public Optional<String> generate(Node node) {
        if (node.is(this.type))
            return Optional.empty();

        return this.rule.generate(node);
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.rule.lex(input)
                .map(node -> node.retype(this.type));
    }
}
