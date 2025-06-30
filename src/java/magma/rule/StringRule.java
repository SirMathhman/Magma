package magma.rule;

import magma.node.EverythingNode;
import magma.node.MapNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;

import java.util.Optional;

public record StringRule(String key) implements Rule<EverythingNode> {
    private Optional<EverythingNode> lex0(final String input) {
        final var node = new MapNode().withString(this.key, input);
        return Optional.of(node);
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return node.findString(this.key);
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }
}