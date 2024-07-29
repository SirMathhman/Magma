package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

public record SuffixRule(Rule child, String suffix) implements Rule {

    private Result<Node, CompileException> parse1(String input) {
        if (!input.endsWith(suffix)) {
            return new Err<>(new CompileException("Suffix '" + suffix + " not present", input));
        }

        return child.parse(input.substring(0, input.length() - suffix.length())).result()
                .mapErr(err -> new CompileException("Failed to parse suffix child", input, err));
    }

    private Result<String, CompileException> generate1(Node node) {
        return child.generate(node).result()
                .mapValue(value -> value + suffix)
                .mapErr(err -> new NodeException("Failed to attach suffix '" + suffix + "'", node, err));
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