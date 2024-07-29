package magma.app.compile;

import magma.api.Result;

public record StripRule(String left, Rule child, String right) implements Rule {
    private Result<Node, CompileException> parse1(String input) {
        return child.parse(input.strip()).result();
    }

    private Result<String, CompileException> generate1(Node node) {
        return child.generate(node).result().mapValue(value -> {
            var left = node.findString(this.left).orElse("");
            var right = node.findString(this.right).orElse("");
            return left + value + right;
        });
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
