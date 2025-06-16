package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.ResultCompileResultFactory;
import magma.app.compile.node.DisplayableNode;
import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithStrings;

public final class StringRule<Node extends NodeWithStrings<Node> & DisplayableNode> implements Rule<Node> {
    private final String key;
    private final NodeFactory<Node> factory;
    private final CompileResultFactory resultFactory;

    public StringRule(String key, NodeFactory<Node> factory) {
        this.key = key;
        this.factory = factory;
        this.resultFactory = ResultCompileResultFactory.createResultCompileResultFactory();
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return this.resultFactory.fromValue(this.factory.create()
                .strings()
                .with(this.key, input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return node.strings()
                .find(this.key)
                .map(this.resultFactory::fromValue)
                .orElseGet(() -> this.resultFactory.fromStringError("String '" + this.key + "' not present", ""));
    }
}