package magma.app.rule;

import magma.CompileError;
import magma.api.Ok;
import magma.api.Result;
import magma.app.node.MapNode;
import magma.app.node.Node;

public class EmptyRule implements Rule {
    @Override
    public Result<Node, CompileError> lex(String input) {
        return new Ok<>(new MapNode());
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return new Ok<>("");
    }
}
