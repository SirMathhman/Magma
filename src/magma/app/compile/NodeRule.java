package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

public record NodeRule(String propertyKey, Rule child) implements Rule {
    private Result<Node, CompileException> parse1(String input) {
        return child.parse(input).result()
                .mapValue(node -> new Node().withNode(propertyKey, node))
                .mapErr(err -> new CompileException("Failed to build node '" + propertyKey + "'", input, err));
    }

    private Result<String, CompileException> generate1(Node node) {
        return node.findNode(propertyKey)
                .map(node1 -> child.generate(node1).result())
                .orElseGet(() -> new Err<>(new NodeException("Property '" + propertyKey + "' not present as a node", node)));
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