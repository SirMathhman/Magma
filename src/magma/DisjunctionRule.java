package magma;

import magma.api.Err;
import magma.api.Result;

import java.util.List;

public record DisjunctionRule(List<Rule> rules) implements Rule {

    @Override
    public Result<Node, ParseException> parse(String input) {
        for (Rule rule : rules) {
            var result = rule.parse(input);
            if (result.isOk()) return result;
        }

        return new Err<>(new ParseException("No valid rules found for", input));
    }

    @Override
    public Result<String, GenerateException> generate(Node node) {
        for (Rule rule : rules) {
            var generated = rule.generate(node);
            if (generated.isOk()) return generated;
        }

        return new Err<>(new GenerateException("No valid rules found for", node));
    }
}
