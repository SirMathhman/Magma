package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
import magma.api.Err;
import magma.api.Result;

import java.util.List;

public record DisjunctionRule(List<Rule> rules) implements Rule {

    private Result<Node, ParseException> parse1(String input) {
        for (Rule rule : rules) {
            var result = rule.parse(input).result();
            if (result.isOk()) return result;
        }

        return new Err<>(new ParseException("No valid rules found for", input));
    }

    private Result<String, GenerateException> generate1(Node node) {
        for (Rule rule : rules) {
            var generated = rule.generate(node).result();
            if (generated.isOk()) return generated;
        }

        return new Err<>(new GenerateException("No valid rules found for", node));
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
