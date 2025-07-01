package magma.rule;

import magma.compile.result.NodeResultFactory;
import magma.compile.result.StringResultFactory;
import magma.node.NodeWithStrings;
import magma.node.factory.NodeFactory;

public final class StringRule<Node extends NodeWithStrings<Node>, Error, NodeResult, StringResult, ResultFactory extends NodeResultFactory<Node, Error, NodeResult> & StringResultFactory<Node, Error, StringResult>>
        implements Rule<Node, NodeResult, StringResult> {
    private final String key;
    private final ResultFactory resultFactory;
    private final NodeFactory<Node> nodeFactory;

    public StringRule(final String key, final ResultFactory resultFactory, final NodeFactory<Node> nodeFactory) {
        this.key = key;
        this.resultFactory = resultFactory;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public NodeResult lex(final String input) {
        final var node = this.nodeFactory.createNode().withString(this.key, input);
        return this.resultFactory.createNode(node);
    }

    @Override
    public StringResult generate(final Node node) {
        return node.findString(this.key)
                   .map(this.resultFactory::createString)
                   .orElseGet(
                           () -> this.resultFactory.createStringError("String '" + this.key + "' not present", node));
    }
}