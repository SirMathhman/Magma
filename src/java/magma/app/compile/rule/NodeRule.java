package magma.app.compile.rule;

import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.attribute.NodeWithNodes;

import java.util.Optional;

public final class NodeRule<Node extends NodeWithNodes<Node>> implements Rule<Node> {
    private final String key;
    private final Rule<Node> rule;
    private final NodeFactory<Node> factory;

    public NodeRule(String key, Rule<Node> rule, NodeFactory<Node> factory) {
        this.key = key;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public Optional<String> generate(Node node) {
        return node.findNode(this.key)
                .flatMap(this.rule::generate);
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.rule.lex(input)
                .map(node -> this.factory.create()
                        .withNode(this.key, node));
    }
}
