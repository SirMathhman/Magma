package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        return Optional.of(new Node().withString(this.key, input));
    }
}