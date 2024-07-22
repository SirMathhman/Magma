package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.CompileException;

public record RightRule(Rule child, String suffix) implements Rule {
    @Override
    public Result<Node, CompileException> parse(String input) {
        if (input.endsWith(suffix)) return child.parse(input.substring(0, input.length() - suffix.length()));
        return new Err<>(new CompileException("No suffix of '" + suffix + "' present", input));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return child.generate(node).mapValue(inner -> inner + suffix);
    }
}