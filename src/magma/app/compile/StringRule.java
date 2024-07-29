package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record StringRule(String propertyKey) implements Rule {
    @Override
    public CompileResult<Node> parse(String input) {
        return new CompileResult<>(new Ok<>(new Node().withString(propertyKey, input)));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return node.findString(propertyKey)
                .map(value -> new CompileResult<>(new Ok<>(value)))
                .orElseGet(() -> new CompileResult<>(new Err<>(new NodeException("String '" + propertyKey + "' was not present", node))));
    }
}