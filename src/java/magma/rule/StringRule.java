package magma.rule;

import magma.node.EverythingNode;
import magma.node.MapNode;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class StringRule implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private final String key;
    private final ResultFactory resultFactory;

    public StringRule(final String key, final ResultFactory resultFactory) {
        this.key = key;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.resultFactory.fromNode(new MapNode())
                .map(node -> node.withString(this.key, input));
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return node.findString(this.key)
                .map(this.resultFactory::fromString)
                .orElseGet(() -> this.resultFactory.fromStringError("String '" + this.key + "' not present", node));
    }
}