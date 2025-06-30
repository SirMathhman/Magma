package magma.rule;

import magma.error.CompileError;
import magma.node.EverythingNode;
import magma.node.MapNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.Optional;

public record StringRule(String key) implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private Optional<EverythingNode> lex0(final String input) {
        final var node = new MapNode().withString(this.key, input);
        return Optional.of(node);
    }

    private Optional<String> generate0(final EverythingNode node) {
        return node.findString(this.key);
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(
                () -> new NodeErr<EverythingNode>(new CompileError(this.getClass().getName(), input)));
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.generate0(node).<StringResult>map(StringOk::new).orElseGet(() -> new StringErr(new CompileError(
                this.getClass().getName(), "?")));
    }
}