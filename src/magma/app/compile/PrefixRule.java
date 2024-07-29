package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record PrefixRule(String slice, Rule child) implements Rule {
    private Optional<Node> parse0(String input) {
        if (!input.startsWith(slice())) return Optional.empty();
        var afterType = input.substring(slice().length());
        return this.child().parse(afterType).findValue();
    }

    private Optional<String> generate0(Node node) {
        return child.generate(node).findValue().map(inner -> slice + inner);
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