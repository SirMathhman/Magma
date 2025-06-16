package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithStrings;

public final class StringRule<Node extends NodeWithStrings<Node> & DisplayableNode, Error, NodeResult> implements Rule<Node, NodeResult, StringResult<Error>> {
    private final String key;
    private final NodeFactory<Node> nodeFactory;
    private final CompileResultFactory<Node, Error, StringResult<Error>, NodeResult, NodeListResult<Node, Error, NodeResult>> resultFactory;

    public StringRule(String key, NodeFactory<Node> nodeFactory, CompileResultFactory<Node, Error, StringResult<Error>, NodeResult, NodeListResult<Node, Error, NodeResult>> resultFactory) {
        this.key = key;
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.resultFactory.fromNode(this.nodeFactory.create()
                .strings()
                .with(this.key, input));
    }

    @Override
    public StringResult<Error> generate(Node node) {
        return node.strings()
                .find(this.key)
                .map(this.resultFactory::fromString)
                .orElseGet(() -> this.resultFactory.fromNodeError("String '" + this.key + "' not present", node));
    }
}