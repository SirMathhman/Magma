package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

public record TypeRule(String type, Rule child) implements Rule {
    @Override
    public Result<Node, CompileException> parse(String input) {
        return child.parse(input).mapValue(node -> node.retype(type));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        if (!node.is(type)) return new Err<>(new NodeException("Expected type '" + type + "'", node));
        return child.generate(node);
    }
}
