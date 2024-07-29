package magma.app.compile;

import magma.api.Err;

public record SuffixRule(Rule child, String suffix) implements Rule {
    @Override
    public CompileResult<Node> parse(String input) {
        if (!input.endsWith(suffix)) {
            return new CompileResult<>(new Err<>(new CompileException("Suffix '" + suffix + " not present", input)));
        }

        return child.parse(input.substring(0, input.length() - suffix.length()))
                .wrapErr(() -> new CompileException("Failed to parse suffix child", input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return child.generate(node)
                .wrapValue(value -> value + suffix)
                .wrapErr(() -> new NodeException("Failed to attach suffix '" + suffix + "'", node));
    }
}