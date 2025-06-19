package magma.app.compile.rule.extract;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.attribute.NodeWithNodes;
import magma.app.compile.rule.action.Generator;
import magma.app.compile.rule.action.Lexer;

import java.util.Optional;

public class NodeExtractor<Node extends NodeWithNodes<Node>, Rule extends Lexer<Node, NodeResult<Node>> & Generator<Node, StringResult>> implements
        Extractor<Node, Node> {
    private final Rule rule;

    public NodeExtractor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public Node attach(Node node, String key, Node value) {
        return node.withNode(key, value);
    }

    @Override
    public Optional<Node> lex(String input) {
        return (this.rule).lex(input)
                .findValue();
    }

    @Override
    public Optional<Node> fromNode(Node node, String key) {
        return node.findNode(key);
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node)
                .findValue();
    }
}