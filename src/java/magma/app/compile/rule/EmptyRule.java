package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.NodeFactory;

public class EmptyRule<Node, Error, NodeResult> implements Rule<Node, Error, NodeResult> {
    private final NodeFactory<Node> nodeFactory;
    private final CompileResultFactory<Node, Error, StringResult<Error>, NodeResult, NodeListResult<Node, Error, NodeResult>> resultFactory;

    public EmptyRule(NodeFactory<Node> nodeFactory, CompileResultFactory<Node, Error, StringResult<Error>, NodeResult, NodeListResult<Node, Error, NodeResult>> resultFactory) {
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.resultFactory.fromNode(this.nodeFactory.create());
    }

    @Override
    public StringResult<Error> generate(Node node) {
        return this.resultFactory.fromString("");
    }
}
