package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record NodeRule(String propertyKey, Rule child) implements Rule {

    private Optional<String> generate0(Node node) {
        return node.findNode(propertyKey).flatMap(node1 -> child.generate(node1).findValue());
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return child.parse(input)
                .mapValue(node -> new Node().withNode(propertyKey, node))
                .mapErr(err -> new CompileException("Failed to build node '" + propertyKey + "'", input, err));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate0(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new NodeException("Invalid node.", node)));
    }
}