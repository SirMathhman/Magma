package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.CompileException;

public record LeftRule(String slice, Rule child) implements Rule {
    @Override
    public Result<Node, CompileException> parse(String input) {
        if (input.startsWith(slice)) return child.parse(input.substring(slice.length()));
        return new Err<>(new CompileException("Input did not start with slice '" + slice + "'", input));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return child.generate(node).mapValue(inner -> slice + inner);
    }
}