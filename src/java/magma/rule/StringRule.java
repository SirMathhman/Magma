package magma.rule;

import magma.factory.ResultFactory;
import magma.node.NodeFactory;
import magma.node.StringNode;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class StringRule<Node extends StringNode<Node>> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final String key;
    private final ResultFactory<Node> resultFactory;
    private final NodeFactory<Node> nodeFactory;

    public StringRule(final String key, final NodeFactory<Node> nodeFactory, final ResultFactory<Node> resultFactory) {
        this.key = key;
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node> lex(final String input) {
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