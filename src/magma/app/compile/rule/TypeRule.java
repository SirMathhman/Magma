package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.Objects;

public final class TypeRule implements Rule {
    private final String type;
    private final Rule child;

    public TypeRule(String type, Rule child) {
        this.type = type;
        this.child = child;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return child.parse(input)
                .wrapValue(node -> node.retype(type))
                .wrapErr(() -> new ParseError("Cannot assign type '" + type + "'", input));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return node.is(type)
                ? generateValid(node)
                : generateInvalid(node);
    }

    private RuleResult<String, GenerateError> generateValid(Node node) {
        return child.generate(node).wrapErr(() -> {
            var format = "Cannot generate with type '%s'";
            var message = format.formatted(type);
            return new GenerateError(message, node);
        });
    }

    private RuleResult<String, GenerateError> generateInvalid(Node node) {
        var format = "Expected type '%s' not present";
        var message = format.formatted(type);
        return RuleResult.RuleResult(Err.Err(new GenerateError(message, node)));
    }

    public String type() {
        return type;
    }

    public Rule child() {
        return child;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TypeRule) obj;
        return Objects.equals(this.type, that.type) &&
               Objects.equals(this.child, that.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, child);
    }

    @Override
    public String toString() {
        return "TypeRule[" +
               "type=" + type + ", " +
               "child=" + child + ']';
    }

}
