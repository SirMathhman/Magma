package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

public record SuffixRule(Rule child, String suffix) implements Rule {

    @Override
    public Result<Node, CompileException> parse(String input) {
        if (!input.endsWith(suffix)) {
            return new Err<>(new CompileException("Suffix '" + suffix + " not present", input));
        }

        return child.parse(input.substring(0, input.length() - suffix.length()))
                .mapErr(err -> new CompileException("Failed to parse suffix child", input, err));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return child.generate(node)
                .mapValue(value -> value + suffix)
                .mapErr(err -> new NodeException("Failed to attach suffix '" + suffix + "'", node, err));
    }
}