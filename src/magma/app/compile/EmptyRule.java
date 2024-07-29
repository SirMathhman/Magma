package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public class EmptyRule implements Rule{
    public static final Rule EMPTY = new EmptyRule();

    private Optional<Node> parse0(String input) {
        return input.isEmpty() ? Optional.of(new Node()) : Optional.empty();
    }

    private Optional<String> generate0(Node node) {
        return Optional.of("");
    }

    private Result<Node, CompileException> parse1(String input) {
        return parse0(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    private Result<String, CompileException> generate1(Node node) {
        return generate0(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new NodeException("Invalid node.", node)));
    }

    @Override
    public CompileResult<Node> parse(String input) {
        return new CompileResult<>(parse1(input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return new CompileResult<>(generate1(node));
    }
}
