package magma.app.compile;

import magma.api.Result;

public record StripRule(String left, Rule child, String right) implements Rule {
    @Override
    public Result<Node, CompileException> parse(String input) {
        return child.parse(input.strip());
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return child.generate(node).mapValue(value -> {
            var left = node.findString(this.left).orElse("");
            var right = node.findString(this.right).orElse("");
            return left + value + right;
        });
    }
}
