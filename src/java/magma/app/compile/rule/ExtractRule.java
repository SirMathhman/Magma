package magma.app.compile.rule;

import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.attribute.NodeWithNodes;
import magma.app.compile.rule.extract.Extractor;
import magma.app.compile.rule.extract.NodeExtractor;

import java.util.Optional;

public final class ExtractRule<Node, Value> implements Rule<Node> {
    private final Extractor<Node, Value> extractor;
    private final Rule<Node> rule;
    private final NodeFactory<Node> factory;
    private final String key;

    public ExtractRule(String key, Rule<Node> rule, NodeFactory<Node> factory, Extractor<Node, Value> extractor) {
        this.key = key;
        this.extractor = extractor;
        this.rule = rule;
        this.factory = factory;
    }

    public static <Node extends NodeWithNodes<Node>> Rule<Node> Node(String key, Rule<Node> rule, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, rule, factory, new NodeExtractor<>());
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.extractor.fromNode(node, this.key)
                .flatMap((Value value) -> this.extractor.generate(value, this.rule));
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.extractor.fromString(input, this.rule)
                .map(child -> {
                    final Node node = this.factory.create();
                    return this.extractor.attach(node, this.key, child);
                });
    }
}
