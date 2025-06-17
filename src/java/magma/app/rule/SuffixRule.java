package magma.app.rule;

import magma.app.node.Node;

import java.util.Optional;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        if (!input.endsWith(this.suffix()))
            return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - this.suffix()
                .length());
        return this.rule()
                .lex(withoutEnd);
    }
}