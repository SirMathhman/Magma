package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
import magma.api.Err;
import magma.api.Result;

public record TypeRule(String type, Rule child) implements Rule {

    private Result<Node, ParseException> parse1(String input) {
        return child.parse(input).result()
                .mapValue(node -> node.retype(type))
                .mapErr(err -> new ParseException("Cannot assign type '" + type + "'", input, err));
    }

    private Result<String, GenerateException> generate1(Node node) {
        return node.is(type) ? child.generate(node).result().mapErr(err -> new GenerateException("Cannot generate with type '" + type + "'", node, err)) : new Err<String, GenerateException>(new GenerateException("Expected type '" + type + "' not present", node));
    }

    @Override
    public RuleResult<Node, ParseException> parse(String input) {
        return new RuleResult<>(parse1(input));
    }

    @Override
    public RuleResult<String, GenerateException> generate(Node node) {
        return new RuleResult<>(generate1(node));
    }
}
