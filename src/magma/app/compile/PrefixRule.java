package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record PrefixRule(String slice, Rule child) implements Rule {

    private Optional<String> generate0(Node node) {
        return child.generate(node).findValue().map(inner -> slice + inner);
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        if (!input.startsWith(this.slice))
            return new Err<>(new CompileException("Input did not start with '" + slice + "'", input));

        var afterType = input.substring(this.slice.length());
        return this.child.parse(afterType);
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate0(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new NodeException("Invalid node.", node)));
    }
}