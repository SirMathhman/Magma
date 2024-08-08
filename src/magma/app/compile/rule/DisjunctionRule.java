package magma.app.compile.rule;

import magma.api.Err;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class DisjunctionRule implements Rule {
    private final List<Rule> rules;

    public DisjunctionRule(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        var errors = new ArrayList<RuleResult<Node, ParseError>>();
        Iterator<Rule> iterator = rules.iterator();
        while (iterator.hasNext()) {
            Rule rule = iterator.next();
            var result = rule.parse(input);
            if (result.isValid()) return result;
            errors.add(result);
        }

        return new RuleResult<>(Err.Err(new ParseError("No valid rules found for", input)), errors);
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        var errors = new ArrayList<RuleResult<String, GenerateError>>();
        Iterator<Rule> iterator = rules.iterator();
        while (iterator.hasNext()) {
            Rule rule = iterator.next();
            var generated = rule.generate(node);
            if (generated.isValid()) return generated;
            errors.add(generated);
        }

        return new RuleResult<>(Err.Err(new GenerateError("No valid rules found for", node)), errors);
    }
}
