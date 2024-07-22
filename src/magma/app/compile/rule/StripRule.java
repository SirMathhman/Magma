package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.CompileException;

public record StripRule(String left, Rule child, String right) implements Rule {
    @Override
    public Result<Node, CompileException> parse(String input) {
        return child.parse(input.strip());
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return child.generate(node).mapValue(inner -> {
            var leftSlice = node.findString(left).orElse("");
            var rightSlice = node.findString(right).orElse("");
            return leftSlice + inner + rightSlice;
        });
    }
}
