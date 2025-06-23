package magma.app.compile.rule;

import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.property.NodeFactory;
import magma.app.compile.node.property.StringNode;
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
        return resultFactory.fromNode(nodeFactory.createNode())
                .map(node -> node.withString(key, input));
    }

    @Override
    public StringResult generate(final Node node) {
        return node.findString(key)
                .map(resultFactory::fromString)
                .orElseGet(() -> resultFactory.fromStringError("String '" + key + "' not present", node));
    }
}