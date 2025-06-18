package magma.app.compile.rule.extract;

import magma.app.compile.node.attribute.NodeWithNodes;
import magma.app.compile.rule.Rule;

import java.util.Optional;

public class NodeExtractor<Node extends NodeWithNodes<Node>> implements Extractor<Node, Node> {
    @Override
    public Node attach(Node node, String key, Node value) {
        return node.withNode(key, value);
    }

    @Override
    public Optional<Node> fromString(String input, Rule<Node> rule) {
        return rule.lex(input);
    }

    @Override
    public Optional<Node> fromNode(Node node, String key) {
        return node.findNode(key);
    }

    @Override
    public Optional<String> generate(Node node, Rule<Node> rule) {
        return rule.generate(node);
    }
}