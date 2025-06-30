package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.Optional;

public record PrefixRule(String prefix, Rule<EverythingNode> rule) implements Rule<EverythingNode> {
    private Optional<EverythingNode> lex0(final String input) {
        if (!input.startsWith(this.prefix)) return Optional.empty();
        final var prefixLength = this.prefix.length();

        final var substring1 = input.substring(prefixLength);
        return this.rule().lex(substring1).toOptional();
    }

    private Optional<String> generate0(final EverythingNode node) {
        return this.rule.generate(node).toOptional().map(result -> this.prefix + result);
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