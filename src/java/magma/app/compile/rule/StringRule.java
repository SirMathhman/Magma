package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.CompileError;
import magma.app.compile.context.NodeContext;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithStrings;

public final class StringRule<Node extends NodeWithStrings<Node> & DisplayableNode> implements Rule<Node> {
    private final String key;
    private final NodeFactory<Node> factory;

    public StringRule(String key, NodeFactory<Node> factory) {
        this.key = key;
        this.factory = factory;
    }

    @Override
    public Result<Node, CompileError> lex(String input) {
        return new Ok<>(this.factory.create()
                .strings()
                .with(this.key, input));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return node.strings()
                .find(this.key)
                .<Result<String, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid rule", new NodeContext(node))));
    }
}