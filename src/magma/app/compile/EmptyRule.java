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

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse0(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate0(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new NodeException("Invalid node.", node)));
    }
}
