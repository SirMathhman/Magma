package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
import magma.api.Err;
import magma.api.Result;

public record TypeRule(String type, Rule child) implements Rule {

    @Override
    public Result<Node, ParseException> parse(String input) {
        return child.parse(input)
                .mapValue(node -> node.retype(type))
                .mapErr(err -> new ParseException("Cannot assign type '" + type + "'", input, err));
    }

    @Override
    public Result<String, GenerateException> generate(Node node) {
        return node.is(type)
                ? child.generate(node).mapErr(err -> new GenerateException("Cannot generate with type '" + type + "'", node, err))
                : new Err<>(new GenerateException("Expected type '" + type + "' not present", node));
    }
}
