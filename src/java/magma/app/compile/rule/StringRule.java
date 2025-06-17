package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public final class StringRule<Error, StringResult> implements Rule<Node, Result<Node, Error>, StringResult> {
    private final String key;
    private final ResultFactory<Node, Result<Node, Error>, StringResult> factory;

    public StringRule(String key, ResultFactory<Node, Result<Node, Error>, StringResult> factory) {
        this.key = key;
        this.factory = factory;
    }

    @Override
    public Result<Node, Error> lex(String input) {
        return this.factory.fromNode(new MapNode().withString(this.key, input));
    }

    @Override
    public StringResult generate(Node node) {
        return node.findString(this.key)
                .map(this.factory::fromString)
                .orElseGet(() -> this.factory.fromNodeErr("String '" + this.key + "' not present", node));
    }
}