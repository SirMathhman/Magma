package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

public record TypeRule(String type, Rule child) implements Rule {
    private Result<Node, CompileException> parse1(String input) {
        return child.parse(input).result().mapValue(node -> node.retype(type));
    }

    private Result<String, CompileException> generate1(Node node) {
        if (!node.is(type)) return new Err<>(new NodeException("Expected type '" + type + "'", node));
        return child.generate(node).result();
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
