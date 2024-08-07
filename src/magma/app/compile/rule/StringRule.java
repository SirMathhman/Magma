package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.Objects;

public final class StringRule implements Rule {
    private final String propertyKey;

    public StringRule(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return RuleResult.RuleResult(new Ok<>(new Node().withString(this.propertyKey(), input)));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return RuleResult.RuleResult(node.findString(propertyKey)
                .<Result<String, GenerateError>>map(Ok::new)
                .orElseGet(() -> Err.Err(new GenerateError("String property '" + propertyKey + "' not present", node))));
    }

    public String propertyKey() {
        return propertyKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StringRule) obj;
        return Objects.equals(this.propertyKey, that.propertyKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyKey);
    }

    @Override
    public String toString() {
        return "StringRule[" +
               "propertyKey=" + propertyKey + ']';
    }

}