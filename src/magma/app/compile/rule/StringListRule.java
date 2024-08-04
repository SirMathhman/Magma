package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.ArrayList;
import java.util.List;

public record StringListRule(String propertyKey, String delimiter) implements Rule {
    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        var items = split(input);
        if (items.isEmpty()) return new RuleResult<>(new Err<>(new ParseError("No items present", input)));
        return new RuleResult<>(new Ok<>(new Node().withStringList(propertyKey, items)));
    }

    private List<String> split(String input) {
        List<String> result = new ArrayList<>();
        int index;
        while ((index = input.indexOf(delimiter)) != -1) {
            result.add(input.substring(0, index));
            input = input.substring(index + delimiter.length());
        }
        result.add(input);
        return result;
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return node.findStringList(propertyKey)
                .map(stringList -> new RuleResult<>(new Ok<String, GenerateError>(String.join(delimiter, stringList))))
                .orElseGet(() -> new RuleResult<>(new Err<>(new GenerateError("String list property '" + propertyKey + "' not present", node))));
    }
}
