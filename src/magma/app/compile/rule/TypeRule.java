package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public record TypeRule(String type, Rule child) implements Rule {
    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return child.parse(input)
                .wrapValue(node -> node.retype(type))
                .wrapErr(err -> new ParseError("Cannot assign type '" + type + "'", input));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return node.is(type)
                ? generateValid(node)
                : generateInvalid(node);
    }

    private RuleResult<String, GenerateError> generateValid(Node node) {
        return child.generate(node).wrapErr(err -> {
            var format = "Cannot generate with type '%s'";
            var message = format.formatted(type);
            return new GenerateError(message, node);
        });
    }

    private RuleResult<String, GenerateError> generateInvalid(Node node) {
        var format = "Expected type '%s' not present";
        var message = format.formatted(type);
        return new RuleResult<>(new Err<>(new GenerateError(message, node)));
    }
}
