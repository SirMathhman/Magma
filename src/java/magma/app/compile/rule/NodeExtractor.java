package magma.app.compile.rule;

import magma.app.compile.node.attribute.NodeWithNodes;

import java.util.Optional;

public class NodeExtractor<Node extends NodeWithNodes<Node>> implements Extractor<Node, Node> {
    private final Rule<Node> rule;

    public NodeExtractor(Rule<Node> rule) {
        this.rule = rule;
    }

    @Override
    public Node attach(Node node, String key, Node value) {
        return node.withNode(key, value);
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.rule.lex(input);
    }

    @Override
    public Optional<Node> fromNode(Node node, String key) {
        return node.findNode(key);
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node);
    }
}