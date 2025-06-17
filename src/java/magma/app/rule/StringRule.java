package magma.app.rule;

import magma.CompileError;
import magma.NodeContext;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.node.MapNode;
import magma.app.node.Node;

public record StringRule(String key) implements Rule {
    @Override
    public Result<Node, CompileError> lex(String input) {
        return new Ok<>(new MapNode().withString(this.key, input));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return node.findString(this.key)
                .<Result<String, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("String '" + this.key + "' not present", new NodeContext(node))));
    }
}