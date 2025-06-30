package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;

import java.util.Optional;

public record SuffixRule(Rule<EverythingNode> rule, String suffix) implements Rule<EverythingNode> {
    private Optional<EverythingNode> lex0(final String input) {
        final var length = input.length();
        if (!input.endsWith(this.suffix)) return Optional.empty();
        final var suffixLength = this.suffix.length();
        final var substring = input.substring(0, length - suffixLength);
        return this.rule().lex(substring).toOptional();
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.rule.generate(node).map(result -> this.suffix + result);
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }
}