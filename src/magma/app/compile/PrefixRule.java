package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

public record PrefixRule(String slice, Rule child) implements Rule {
    @Override
    public CompileResult<Node> parse(String input) {
        if (!input.startsWith(this.slice))
            return new CompileResult<>(new Err<>(new CompileException("Input did not start with '" + slice + "'", input)));

        var afterType = input.substring(this.slice.length());
        return this.child.parse(afterType);
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return child.generate(node)
                .wrapValue(inner -> slice + inner)
                .wrapErr(() -> new NodeException("Invalid node.", node));
    }
}