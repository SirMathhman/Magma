package magma.app.compile;

import magma.app.compile.node.Node;

import java.util.Optional;

public record TypeRule(String type, Rule rule) implements Rule {
    @Override
    public Optional<String> generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);

        return Optional.empty();
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.rule.lex(input)
                .map(node -> node.retype(this.type));
    }
}
