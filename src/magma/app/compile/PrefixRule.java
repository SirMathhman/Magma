package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record PrefixRule(String slice, Rule child) implements Rule {

    private Optional<String> generate0(Node node) {
        return child.generate(node).result().findValue().map(inner -> slice + inner);
    }

    private Result<Node, CompileException> parse1(String input) {
        if (!input.startsWith(this.slice))
            return new Err<>(new CompileException("Input did not start with '" + slice + "'", input));

        var afterType = input.substring(this.slice.length());
        return this.child.parse(afterType).result();
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