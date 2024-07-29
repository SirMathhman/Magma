package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

import java.util.ArrayList;
import java.util.List;

public record DisjunctionRule(List<Rule> rules) implements Rule {
    @Override
    public CompileResult<Node> parse(String input) {
        var errors = new ArrayList<CompileResult<Node>>();
        for (Rule rule : rules) {
            var parsed = rule.parse(input);
            if (parsed.isValid()) return parsed;
            errors.add(parsed);
        }

        if (errors.isEmpty()) {
            return new CompileResult<>(new Err<>(new CompileException("No rules present", input)));
        } else {
            return new CompileResult<>(new Err<>(new CompileException("Failed to apply disjunction", input)), errors);
        }
    }

    @Override
    public CompileResult<String> generate(Node node) {
        var errors = new ArrayList<CompileResult<String>>();
        for (Rule rule : rules) {
            var generated = rule.generate(node);
            if (generated.isValid()) return generated;
            errors.add(generated);
        }

        if (errors.isEmpty()) {
            return new CompileResult<>(new Err<>(new NodeException("No rules present", node)));
        } else {
            return new CompileResult<>(new Err<>(new NodeException("Failed to apply disjunction", node)), errors);
        }
    }
}
