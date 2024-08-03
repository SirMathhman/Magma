package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public record NodeRule(String propertyKey, Rule child) implements Rule {

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return this.child()
                .parse(input)
                .wrapValue(node -> new Node().withNode(this.propertyKey(), node));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return node.findNode(propertyKey)
                .map(child::generate)
                .orElseGet(() -> new RuleResult<>(new Err<>(new GenerateError("Node property '" + propertyKey + "' not present", node))));
    }
}