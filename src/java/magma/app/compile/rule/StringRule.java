package magma.app.compile.rule;

import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithStrings;

import java.util.Optional;

public final class StringRule<Node extends NodeWithStrings<Node>> implements Rule<Node> {
    private final String key;
    private final NodeFactory<Node> factory;

    public StringRule(String key, NodeFactory<Node> factory) {
        this.key = key;
        this.factory = factory;
    }

    @Override
    public Optional<Node> lex(String input) {
        return Optional.of(this.factory.create()
                .strings()
                .with(this.key, input));
    }

    @Override
    public Optional<String> generate(Node node) {
        return node.strings()
                .find(this.key);
    }
}