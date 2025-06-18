package magma.app.compile.rule;

import magma.app.compile.node.attribute.NodeWithType;

import java.util.Optional;

public final class TypeRule<Node extends NodeWithType<Node>> implements Rule<Node> {
    private final String type;
    private final Rule<Node> rule;

    public TypeRule(String type, Rule<Node> rule) {
        this.type = type;
        this.rule = rule;
    }

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
