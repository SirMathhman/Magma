package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public final class NodeRule implements Rule {
    private final String propertyKey;
    private final Rule child;

    public NodeRule(String propertyKey, Rule child) {
        this.propertyKey = propertyKey;
        this.child = child;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return child
                .parse(input)
                .wrapValue(node -> new Node().withNode(propertyKey, node));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return node.findNode(propertyKey)
                .map(child::generate)
                .orElseGet(() -> new RuleResult<>(new Err<>(new GenerateError("Node property '" + propertyKey + "' not present", node))));
    }
}