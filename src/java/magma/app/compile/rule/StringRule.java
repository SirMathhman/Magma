package magma.app.compile.rule;

import magma.app.compile.error.NodeResult;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public final class StringRule<StringResult> implements Rule<Node, NodeResult, StringResult> {
    private final String key;
    private final ResultFactory<Node, NodeResult, StringResult> factory;

    public StringRule(String key, ResultFactory<Node, NodeResult, StringResult> factory) {
        this.key = key;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.factory.fromNode(new MapNode().withString(this.key, input));
    }

    @Override
    public StringResult generate(Node node) {
        return node.findString(this.key)
                .map(this.factory::fromString)
                .orElseGet(() -> this.factory.fromNodeErr("String '" + this.key + "' not present", node));
    }
}