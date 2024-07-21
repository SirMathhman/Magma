package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileException;

import java.util.Map;
import java.util.Optional;

public record ExtractRule(String propertyKey) implements Rule {
    private Optional<Map<String, String>> parse0(String input) {
        return Optional.of(Map.of(propertyKey(), input));
    }

    private Optional<String> generate0(Map<String, String> node) {
        return node.containsKey(propertyKey()) ? Optional.of(node.get(propertyKey())) : Optional.empty();
    }

    private Optional<Node> parse1(String input) {
        return parse0(input).map(strings -> new Node(Optional.empty(), strings));
    }

    private Optional<String> generate1(Node node) {
        return generate0(node.strings());
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse1(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate1(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Cannot generate", node.toString())));
    }
}