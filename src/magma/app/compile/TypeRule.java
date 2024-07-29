package magma.app.compile;

import magma.api.Err;

public record TypeRule(String type, Rule child) implements Rule {
    @Override
    public CompileResult<Node> parse(String input) {
        return child.parse(input).wrapValue(node -> node.retype(type));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        if (!node.is(type)) return new CompileResult<>(new Err<>(new NodeException("Expected type '" + type + "'", node)));
        return child.generate(node);
    }
}
