package magma;

import java.util.Optional;

public class EmptyRule implements Rule {
    public static final Rule EMPTY_RULE = new EmptyRule();

    private EmptyRule() {
    }

    private Optional<Node> parse0(String input) {
        return input.isEmpty() ? Optional.of(new Node()) : Optional.empty();
    }


    @Override
    public Result<Node, ParseException> parse(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    @Override
    public Result<String, GeneratingException> generate(Node node) {
        return new Ok<>("");
    }
}
