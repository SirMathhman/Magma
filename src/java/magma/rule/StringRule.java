package magma.rule;

import magma.error.CompileError;
import magma.error.NodeContext;
import magma.node.EverythingNode;
import magma.node.MapNode;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.StringErr;
import magma.string.StringOk;
import magma.string.StringResult;

public record StringRule(String key) implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return new NodeOk<>(new MapNode().withString(this.key(), input));
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return node.findString(this.key)
                .<StringResult>map(StringOk::new)
                .orElseGet(() -> new StringErr(new CompileError("String '" + this.key + "' not present",
                        new NodeContext(node))));
    }
}