package magma.rule;

import magma.api.error.ErrorSequence;
import magma.api.error.FormattedError;
import magma.factory.ResultFactory;
import magma.node.NodeFactory;
import magma.node.StringNode;
import magma.node.result.Mapping;

public final class StringRule<Node extends StringNode<Node>, NodeResult extends Mapping<Node, NodeResult>, StringResult> implements
        Rule<Node, NodeResult, StringResult> {
    private final String key;
    private final ResultFactory<Node, NodeResult, StringResult, ErrorSequence<FormattedError>> resultFactory;
    private final NodeFactory<Node> nodeFactory;

    public StringRule(final String key, final NodeFactory<Node> nodeFactory, final ResultFactory<Node, NodeResult, StringResult, ErrorSequence<FormattedError>> resultFactory) {
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