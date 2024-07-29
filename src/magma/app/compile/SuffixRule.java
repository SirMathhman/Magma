package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record SuffixRule(Rule child, String suffix) implements Rule {
    private Optional<Node> parse0(String input) {
        if (input.endsWith(suffix))
            return child.parse(input.substring(0, input.length() - suffix.length())).findValue();
        return Optional.empty();
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse0(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return child.generate(node)
                .mapValue(value -> value + suffix)
                .mapErr(err -> new NodeException("Failed to attach suffix '" + suffix + "'", node, err));
    }
}