package magma.app.compile.rule;

import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.node.attribute.NodeWithNodes;
import magma.app.compile.node.attribute.NodeWithStrings;

import java.util.Optional;

public final class ExtractRule<Node, Value> implements Rule<Node> {
    private final Extractor<Node, Value> extractor;
    private final NodeFactory<Node> factory;
    private final String key;

    public ExtractRule(String key, NodeFactory<Node> factory, Extractor<Node, Value> extractor) {
        this.key = key;
        this.extractor = extractor;
        this.factory = factory;
    }

    public static <Node extends NodeWithNodes<Node>> Rule<Node> Node(String key, Rule<Node> rule, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new NodeExtractor<>(rule));
    }

    public static <Node extends NodeWithStrings<Node>> Rule<Node> createStringRule(String key, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new StringExtractor<>());
    }

    public static <Node extends NodeWithNodeLists<Node>> Rule<Node> NodeList(String key, Rule<Node> rule, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new NodeListExtractor<>(rule));
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.extractor.fromNode(node, this.key)
                .flatMap(this.extractor::generate);
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.extractor.lex(input)
                .map(child -> {
                    final Node node = this.factory.create();
                    return this.extractor.attach(node, this.key, child);
                });
    }
}
