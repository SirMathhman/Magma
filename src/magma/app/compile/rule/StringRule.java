package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public final class StringRule implements Rule {
    private final String propertyKey;

    public StringRule(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return RuleResult.RuleResult(new Ok<>(new Node().withString(propertyKey, input)));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return RuleResult.RuleResult(node.findString(propertyKey)
                .<Result<String, GenerateError>>map(Ok::new)
                .orElseGet(() -> Err.Err(new GenerateError("String property '" + propertyKey + "' not present", node))));
    }
}