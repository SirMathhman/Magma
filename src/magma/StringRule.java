package magma;

import java.util.Optional;

public record StringRule(String propertyKey) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        return Optional.of(new Node().withString(propertyKey(), input));
    }
}