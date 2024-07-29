package magma.app.compile;

public record StripRule(String left, Rule child, String right) implements Rule {
    @Override
    public CompileResult<Node> parse(String input) {
        return child.parse(input.strip());
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return child.generate(node).wrapValue(value -> {
            var left1 = node.findString(this.left).orElse("");
            var right1 = node.findString(this.right).orElse("");
            return left1 + value + right1;
        });
    }
}
