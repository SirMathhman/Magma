package magma.rule;

import magma.node.TypedNode;

import java.util.Optional;

public record TypeRule<Node extends TypedNode<Node>>(String type, Rule<Node> rule) implements Rule<Node> {
    private Optional<Node> lex0(final String input) {
        return this.rule.lex(input).toOptional().map(node -> node.retype(this.type));
    }

    @Override
    public Optional<String> generate(final Node node) {
        if (node.is(this.type)) return this.rule.generate(node);
        return Optional.empty();
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        return this.lex0(input).<NodeResult<Node>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }
}
