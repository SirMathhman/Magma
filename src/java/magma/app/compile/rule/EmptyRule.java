package magma.app.compile.rule;

import magma.app.compile.node.NodeFactory;

import java.util.Optional;

public class EmptyRule<Node> implements Rule<Node> {
    private final NodeFactory<Node> factory;

    public EmptyRule(NodeFactory<Node> factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Node> lex(String input) {
        return Optional.of(this.factory.create());
    }

    @Override
    public Optional<String> generate(Node node) {
        return Optional.of("");
    }
}
