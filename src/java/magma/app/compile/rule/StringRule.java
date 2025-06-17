package magma.app.compile.rule;

import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.NodeWithStrings;

public final class StringRule<Node extends NodeWithStrings<Node>, NodeResult, StringResult> implements Rule<Node, NodeResult, StringResult> {
    private final String key;
    private final ResultFactory<Node, FormattedError, NodeResult, StringResult> resultFactory;
    private final NodeFactory<Node> nodeFactory;

    public StringRule(String key, ResultFactory<Node, FormattedError, NodeResult, StringResult> resultFactory, NodeFactory<Node> nodeFactory) {
        this.key = key;
        this.resultFactory = resultFactory;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.resultFactory.fromNode(this.nodeFactory.create()
                .withString(this.key, input));
    }

    @Override
    public StringResult generate(Node node) {
        return node.findString(this.key)
                .map(this.resultFactory::fromString)
                .orElseGet(() -> this.resultFactory.fromNodeErr("String '" + this.key + "' not present", node));
    }
}