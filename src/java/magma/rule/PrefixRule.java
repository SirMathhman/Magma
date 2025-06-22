package magma.rule;

import magma.node.Node;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.option.Option;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public NodeResult lex(final String input) {
        if (!input.startsWith(this.prefix()))
            return new NodeErr();

        final var slice = input.substring(this.prefix()
                .length());
        return this.rule()
                .lex(slice);
    }

    @Override
    public Option<String> generate(final Node node) {
        return this.rule.generate(node)
                .map(result -> this.prefix + result);
    }
}