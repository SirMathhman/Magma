package magma;

import java.util.Optional;

public record StripRule(Rule child) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        return child.parse(input.strip());
    }
}
