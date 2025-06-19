package magma.app.compile.rule;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.node.attribute.NodeWithNodes;
import magma.app.compile.node.attribute.NodeWithStrings;
import magma.app.compile.rule.action.CompileResults;
import magma.app.compile.rule.extract.Extractor;
import magma.app.compile.rule.extract.NodeExtractor;
import magma.app.compile.rule.extract.NodeListExtractor;
import magma.app.compile.rule.extract.StringExtractor;

import java.util.Optional;

public final class ExtractRule<Node, Value> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final Extractor<Node, Value> extractor;
    private final NodeFactory<Node> factory;
    private final String key;

    public ExtractRule(String key, NodeFactory<Node> factory, Extractor<Node, Value> extractor) {
        this.key = key;
        this.extractor = extractor;
        this.factory = factory;
    }

    public static <Node extends NodeWithNodes<Node>> Rule<Node, NodeResult<Node>, StringResult> Node(String key, Rule<Node, NodeResult<Node>, StringResult> rule, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new NodeExtractor<>(rule));
    }

    public static <Node extends NodeWithStrings<Node>> Rule<Node, NodeResult<Node>, StringResult> createStringRule(String key, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new StringExtractor<>());
    }

    public static <Node extends NodeWithNodeLists<Node>> Rule<Node, NodeResult<Node>, StringResult> NodeList(String key, Rule<Node, NodeResult<Node>, StringResult> rule, NodeFactory<Node> factory) {
        return new ExtractRule<>(key, factory, new NodeListExtractor<>(rule));
    }

    private Optional<String> generate0(Node node) {
        return this.extractor.fromNode(node, this.key)
                .flatMap(this.extractor::generate);
    }

    private Optional<Node> lex0(String input) {
        return this.extractor.lex(input)
                .map(child -> {
                    final Node node = this.factory.create();
                    return this.extractor.attach(node, this.key, child);
                });
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return CompileResults.fromOptionWithString(this.lex0(input), input);
    }

    @Override
    public StringResult generate(Node node) {
        return CompileResults.fromOptionWithNode(this.generate0(node), node);
    }
}
