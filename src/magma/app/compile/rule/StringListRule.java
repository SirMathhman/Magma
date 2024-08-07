package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.ArrayList;
import java.util.List;

public final class StringListRule implements Rule {
    private final String propertyKey;
    private final String delimiter;

    public StringListRule(String propertyKey, String delimiter) {
        this.propertyKey = propertyKey;
        this.delimiter = delimiter;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        var items = split(input);
        var isEmpty = items.isEmpty();
        if (isEmpty) return RuleResult.RuleResult(Err.Err(new ParseError("No items present", input)));
        return RuleResult.RuleResult(new Ok<>(new Node().withStringList(propertyKey, items)));
    }

    private List<String> split(String input) {
        List<String> result = new ArrayList<>();
        int index;
        while (true) {
            index = input.indexOf(delimiter);
            if (index == -1) break;

            result.add(input.substring(0, index));
            var offset = index + delimiter.length();
            input = input.substring(offset);
        }
        result.add(input);
        return result;
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return node.findStringList(propertyKey)
                .map(list -> generateWithList(node, list))
                .orElseGet(() -> RuleResult.RuleResult(Err.Err(new GenerateError("String list property '" + propertyKey + "' not present", node))));
    }

    private RuleResult<String, GenerateError> generateWithList(Node node, List<String> list) {
        var isEmpty = list.isEmpty();
        if (isEmpty) return RuleResult.RuleResult(Err.Err(new GenerateError("List cannot be empty.", node)));
        return RuleResult.RuleResult(new Ok<>(String.join(delimiter, list)));
    }
}
