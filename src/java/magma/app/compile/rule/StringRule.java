package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.CompileError;
import magma.app.compile.CompileResult;
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
    public CompileResult<Node> lex(String input) {
        return CompileResult.from(this.factory.create()
                .strings()
                .with(this.key, input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return node.strings()
                .find(this.key)
                .map(CompileResult::from)
                .orElseGet(() -> new CompileResult<>(new Err<>(new CompileError("Invalid rule", new NodeContext(node)))));
    }
}