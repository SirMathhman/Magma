package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;

public class EmptyRule implements Rule {
    public static final Rule EMPTY = new EmptyRule();

    @Override
    public CompileResult<Node> parse(String input) {
        return input.isEmpty()
                ? new CompileResult<>(new Ok<>(new Node()))
                : new CompileResult<>(new Err<>(new CompileException("Node is not empty", input)));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return new CompileResult<>(new Ok<>(""));
    }
}
