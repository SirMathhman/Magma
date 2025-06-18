package magma.app.compile.rule;

import java.util.Optional;

public record StripRule<Node>(Rule<Node> rule) implements Rule<Node> {
    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node);
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.rule.lex(input.strip());
    }
}
