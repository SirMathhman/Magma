package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

public record NodeRule(String propertyKey, Rule child) implements Rule {
    @Override
    public Result<Node, CompileException> parse(String input) {
        return child.parse(input)
                .mapValue(node -> new Node().withNode(propertyKey, node))
                .mapErr(err -> new CompileException("Failed to build node '" + propertyKey + "'", input, err));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return node.findNode(propertyKey)
                .map(node1 -> child.generate(node1))
                .orElseGet(() -> new Err<>(new NodeException("Property '" + propertyKey + "' not present as a node", node)));
    }
}