package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.ResultCompileResultFactory;
import magma.app.compile.node.NodeFactory;

public class EmptyRule<Node> implements Rule<Node> {
    private final NodeFactory<Node> factory;

    public EmptyRule(NodeFactory<Node> factory) {
        this.factory = factory;
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return ResultCompileResultFactory.createResultCompileResultFactory()
                .fromValue(this.factory.create());
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return ResultCompileResultFactory.createResultCompileResultFactory()
                .fromValue("");
    }
}
