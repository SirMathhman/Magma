package magma.app;

import magma.app.node.Node;

import java.util.Optional;

public record LastRule(StringRule leftRule, String infix, StringRule rightRule) implements Rule {
    @Override
    public Optional<String> generate(Node node) {
        return Optional.of(this.leftRule.generate(node)
                .orElse("") + this.infix + this.rightRule.generate(node)
                .orElse(""));
    }

    @Override
    public Optional<Node> lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0)
            return Optional.empty();

        final var slice = input.substring(index + this.infix.length());
        return this.rightRule.lex(slice);
    }
}