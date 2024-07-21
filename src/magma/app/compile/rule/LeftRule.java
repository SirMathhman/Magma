package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileException;

import java.util.Map;
import java.util.Optional;

public record LeftRule(String slice, Rule child) implements Rule {
    private Optional<Map<String, String>> parse0(String input) {
        if (!input.startsWith(slice)) return Optional.empty();
        return child.parse(input.substring(slice.length())).findValue().map(Node::strings);
    }

    private Optional<String> generate0(Map<String, String> node) {
        return child.generate(new Node(Optional.empty(), node)).findValue().map(inner -> slice + inner);
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