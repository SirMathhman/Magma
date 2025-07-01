package magma.rule;

import magma.compile.result.ResultFactory;
import magma.node.NodeWithStrings;
import magma.node.factory.NodeFactory;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public final class StringRule<Node extends NodeWithStrings<Node>, Error>
        implements Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> {
    private final String key;
    private final ResultFactory<Node, Error, StringResult<Error>> resultFactory;
    private final NodeFactory<Node> nodeFactory;

    public StringRule(final String key,
                      final ResultFactory<Node, Error, StringResult<Error>> resultFactory,
                      final NodeFactory<Node> nodeFactory) {
        this.key = key;
        this.resultFactory = resultFactory;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> lex(final String input) {
        final var node = this.nodeFactory.createNode().withString(this.key, input);
        return this.resultFactory.createNode(node);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return node.findString(this.key)
                   .map(this.resultFactory::createString)
                   .orElseGet(
                           () -> this.resultFactory.createStringError("String '" + this.key + "' not present", node));
    }
}