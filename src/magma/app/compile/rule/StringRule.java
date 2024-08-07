package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public record StringRule(String propertyKey) implements Rule {

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return new RuleResult<>(new Ok<>(new Node().withString(this.propertyKey(), input)));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return new RuleResult<>(node.findString(propertyKey)
                .<Result<String, GenerateError>>map(Ok::new)
                .orElseGet(() -> Err.Err(new GenerateError("String property '" + propertyKey + "' not present", node))));
    }
}