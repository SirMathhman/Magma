package magma.app.compile.rule;

import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.CompileError;
import magma.app.compile.node.NodeFactory;

public class EmptyRule<Node> implements Rule<Node> {
    private final NodeFactory<Node> factory;

    public EmptyRule(NodeFactory<Node> factory) {
        this.factory = factory;
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return new Ok<>(this.factory.create());
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return new Ok<>("");
    }
}
