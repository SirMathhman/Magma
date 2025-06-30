package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.Optional;

public record StripRule(Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rule) implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    private Optional<EverythingNode> lex0(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip).toOptional();
    }

    private Optional<String> generate0(final EverythingNode node) {
        return this.rule.generate(node).toOptional();
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.generate0(node).<StringResult>map(StringOk::new).orElseGet(StringErr::new);
    }
}