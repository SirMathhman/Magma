package magma.app.compile.rule;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactoryImpl;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public record StringRule(
        String key) implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    @Override
    public Result<Node, FormattedError> lex(String input) {
        return new Ok<>(new MapNode().withString(this.key, input));
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return node.findString(this.key)
                .<Result<String, FormattedError>>map(Ok::new)
                .orElseGet(() -> ResultFactoryImpl.create()
                        .fromNodeErr("String '" + this.key + "' not present", node));
    }
}