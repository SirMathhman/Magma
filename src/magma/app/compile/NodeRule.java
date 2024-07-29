package magma.app.compile;

import magma.api.Err;

public record NodeRule(String propertyKey, Rule child) implements Rule {
    @Override
    public CompileResult<Node> parse(String input) {
        return child.parse(input)
                .wrapValue(node -> new Node().withNode(propertyKey, node))
                .wrapErr(() -> new CompileException("Failed to build node '" + propertyKey + "'", input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return node.findNode(propertyKey)
                .map(child::generate)
                .orElseGet(() -> new CompileResult<>(new Err<>(new NodeException("Property '" + propertyKey + "' not present as a node", node))));
    }
}