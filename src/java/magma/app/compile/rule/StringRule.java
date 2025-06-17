package magma.app.compile.rule;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public final class StringRule implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    private final String key;
    private final ResultFactory<Node, Result<Node, FormattedError>, Result<String, FormattedError>> factory;

    public StringRule(String key, ResultFactory<Node, Result<Node, FormattedError>, Result<String, FormattedError>> factory) {
        this.key = key;
        this.factory = factory;
    }

    @Override
    public Result<Node, FormattedError> lex(String input) {
        return this.factory.fromNode(new MapNode().withString(this.key, input));
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return node.findString(this.key)
                .<Result<String, FormattedError>>map(Ok::new)
                .orElseGet(() -> this.factory.fromNodeErr("String '" + this.key + "' not present", node));
    }
}