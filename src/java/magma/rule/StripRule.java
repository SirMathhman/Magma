package magma.rule;

import magma.node.EverythingNode;

import java.util.Optional;

public record StripRule(Rule<EverythingNode> rule) implements Rule<EverythingNode> {
    private Optional<EverythingNode> lex0(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip).toOptional();
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.rule.generate(node);
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }
}