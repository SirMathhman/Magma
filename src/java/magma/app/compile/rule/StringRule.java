package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.context.NodeContext;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.FormattedError;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public record StringRule(String key) implements Rule {
    @Override
    public Result<Node, FormattedError> lex(String input) {
        return new Ok<>(new MapNode().withString(this.key, input));
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return node.findString(this.key)
                .<Result<String, FormattedError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("String '" + this.key + "' not present", new NodeContext(node))));
    }
}