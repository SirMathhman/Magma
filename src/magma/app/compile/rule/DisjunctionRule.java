package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.ArrayList;
import java.util.List;

public record DisjunctionRule(List<Rule> rules) implements Rule {
    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        var errors = new ArrayList<RuleResult<Node, ParseError>>();
        for (Rule rule : rules) {
            var result = rule.parse(input);
            if (result.isValid()) return result;
            errors.add(result);
        }

        return new RuleResult<>(new Err<>(new ParseError("No valid rules found for", input)), errors);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        var errors = new ArrayList<RuleResult<String, GenerateError>>();
        for (Rule rule : rules) {
            var generated = rule.generate(node);
            if (generated.isValid()) return generated;
            errors.add(generated);
        }

        return new RuleResult<>(new Err<>(new GenerateError("No valid rules found for", node)), errors);
    }
}
