package magma.app.compile.rule;

import magma.app.compile.node.NodeFactory;
import magma.app.compile.rule.extract.Extractor;

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
