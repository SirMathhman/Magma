package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;

import java.util.ArrayList;
import java.util.List;

public record DisjunctionRule(List<Rule> rules) implements Rule {
    @Override
    public RuleResult<Node, ParseException> parse(String input) {
        var errors = new ArrayList<RuleResult<Node, ParseException>>();
        for (Rule rule : rules) {
            var result = rule.parse(input);
            if (result.isValid()) return result;
            errors.add(result);
        }

        return new RuleResult<>(new Err<>(new ParseException("No valid rules found for", input)), errors);
    }

    @Override
    public RuleResult<String, GenerateException> generate(Node node) {
        var errors = new ArrayList<RuleResult<String, GenerateException>>();
        for (Rule rule : rules) {
            var generated = rule.generate(node);
            if (generated.isValid()) return generated;
            errors.add(generated);
        }

        return new RuleResult<>(new Err<>(new GenerateException("No valid rules found for", node)), errors);
    }
}
