package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record StringRule(String propertyKey) implements Rule {
    private Optional<Node> parse0(String input) {
        return Optional.of(new Node().withString(propertyKey(), input));
    }

    private Result<Node, CompileException> parse1(String input) {
        return parse0(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    private Result<String, CompileException> generate1(Node node) {
        return node.findString(propertyKey)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new NodeException("String '" + propertyKey + "' was not present", node)));
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