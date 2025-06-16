package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.ResultCompileResult;
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
        return ResultCompileResult.fromValue(this.factory.create()
                .strings()
                .with(this.key, input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return node.strings()
                .find(this.key)
                .map(ResultCompileResult::fromValue)
                .orElseGet(() -> ResultCompileResult.fromStringError("String '" + this.key + "' not present", ""));
    }
}