package magma.app.compile.rule;

import magma.app.compile.CompileResult;
import magma.app.compile.node.NodeFactory;

public class EmptyRule<Node> implements Rule<Node> {
    private final NodeFactory<Node> factory;

    public EmptyRule(NodeFactory<Node> factory) {
        this.factory = factory;
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return CompileResult.from(this.factory.create());
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return CompileResult.from("");
    }
}
