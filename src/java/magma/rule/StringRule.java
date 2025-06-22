package magma.rule;

import magma.factory.ResultFactory;
import magma.node.EverythingNode;
import magma.node.NodeFactory;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class StringRule implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private final String key;
    private final ResultFactory<EverythingNode> resultFactory;
    private final NodeFactory nodeFactory;

    public StringRule(final String key, final NodeFactory nodeFactory, final ResultFactory<EverythingNode> resultFactory) {
        this.key = key;
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.resultFactory.fromNode(this.nodeFactory.createNode())
                .map(node -> node.withString(this.key, input));
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return node.findString(this.key)
                .map(this.resultFactory::fromString)
                .orElseGet(() -> this.resultFactory.fromStringError("String '" + this.key + "' not present", node));
    }
}