package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;

import java.util.Optional;

public record PrefixRule(String prefix, Rule<EverythingNode> rule) implements Rule<EverythingNode> {
    private Optional<EverythingNode> lex0(final String input) {
        if (!input.startsWith(this.prefix)) return Optional.empty();
        final var prefixLength = this.prefix.length();

        final var substring1 = input.substring(prefixLength);
        return this.rule().lex(substring1).toOptional();
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.rule.generate(node).map(result -> this.prefix + result);
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }
}