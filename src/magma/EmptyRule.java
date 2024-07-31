package magma;

import java.util.Optional;

public class EmptyRule implements Rule {
    public static final Rule EMPTY_RULE = new EmptyRule();

    private EmptyRule() {
    }

    @Override
    public Optional<Node> parse(String input) {
        return input.isEmpty() ? Optional.of(new Node()) : Optional.empty();
    }
}
