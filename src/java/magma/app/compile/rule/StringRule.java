package magma.app.compile.rule;

import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.StringNode;
import magma.app.compile.node.result.Mapping;

public final class StringRule<Node extends StringNode<Node>, NodeResult extends Mapping<Node, NodeResult>, ErrorSequence, StringResult> implements
        Rule<Node, NodeResult, StringResult> {
    private final String key;
    private final ResultFactory<Node, NodeResult, StringResult, ErrorSequence> resultFactory;
    private final NodeFactory<Node> nodeFactory;

    public StringRule(final String key, final NodeFactory<Node> nodeFactory, final ResultFactory<Node, NodeResult, StringResult, ErrorSequence> resultFactory) {
        this.key = key;
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult lex(final String input) {
        return this.resultFactory.fromNode(this.nodeFactory.createNode())
                .map(node -> node.withString(this.key, input));
    }

    @Override
    public StringResult generate(final Node node) {
        return node.findString(this.key)
                .map(this.resultFactory::fromString)
                .orElseGet(() -> this.resultFactory.fromStringError("String '" + this.key + "' not present", node));
    }
}