package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.NodeFactory;

public class EmptyRule<Node, Error> implements Rule<Node, Error> {
    private final NodeFactory<Node> nodeFactory;
    private final CompileResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error>, NodeListResult<Node, Error>> resultFactory;

    public EmptyRule(NodeFactory<Node> nodeFactory, CompileResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error>, NodeListResult<Node, Error>> resultFactory) {
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node, Error> lex(String input) {
        return this.resultFactory.fromNode(this.nodeFactory.create());
    }

    @Override
    public StringResult<Error> generate(Node node) {
        return this.resultFactory.fromString("");
    }
}
