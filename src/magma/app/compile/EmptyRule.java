package magma.app.compile;

import java.util.Optional;

public class EmptyRule implements Rule{
    public static final Rule EMPTY = new EmptyRule();

    @Override
    public Optional<Node> parse(String input) {
        return input.isEmpty() ? Optional.of(new Node()) : Optional.empty();
    }

    @Override
    public Optional<String> generate(Node node) {
        return Optional.of("");
    }
}
