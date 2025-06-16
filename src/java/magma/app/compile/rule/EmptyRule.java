package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.node.NodeFactory;

public class EmptyRule<Node> implements Rule<Node> {
    private final NodeFactory<Node> nodeFactory;
    private final CompileResultFactory<Node> resultFactory;

    public EmptyRule(NodeFactory<Node> nodeFactory, CompileResultFactory<Node> resultFactory) {
        this.nodeFactory = nodeFactory;
        this.resultFactory = resultFactory;
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return this.resultFactory.fromNode(this.nodeFactory.create());
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return this.resultFactory.fromString("");
    }
}
