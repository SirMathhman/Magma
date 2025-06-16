package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.list.NodeListResult;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.NodeFactory;

public class EmptyRule<Node> implements Rule<Node> {
    private final NodeFactory<Node> nodeFactory;
    private final CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> resultFactory;

    public EmptyRule(NodeFactory<Node> nodeFactory, CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> resultFactory) {
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return this.resultFactory.fromNode(this.nodeFactory.create());
    }

    @Override
    public StringResult generate(Node node) {
        return this.resultFactory.fromString("");
    }
}
