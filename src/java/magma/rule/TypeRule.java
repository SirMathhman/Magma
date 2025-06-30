package magma.rule;

import magma.node.TypedNode;

import java.util.Optional;

public record TypeRule<Node extends TypedNode<Node>>(String type, Rule<Node> rule) implements Rule<Node> {
    @Override
    public Optional<Node> lex(final String input) {
        return this.rule.lex(input).map(node -> node.retype(this.type));
    }

    @Override
    public Optional<String> generate(final Node node) {
        if (node.is(this.type)) return this.rule.generate(node);
        return Optional.empty();
    }
}
