package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

import java.util.ArrayList;
import java.util.List;

public record DisjunctionRule(List<Rule> rules) implements Rule {
    @Override
    public Result<Node, CompileException> parse(String input) {
        var errors = new ArrayList<Result<Node, CompileException>>();
        for (Rule rule : rules) {
            var parsed = rule.parse(input);
            if (parsed.isOk()) return parsed;
            errors.add(parsed);
        }

        if (errors.isEmpty()) {
            return new Err<>(new CompileException("No rules present", input));
        } else {
            return errors.get(errors.size() - 1).mapErr(err -> new CompileException("Failed to apply disjunction", input, err));
        }
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        var errors = new ArrayList<Result<String, CompileException>>();
        for (Rule rule : rules) {
            var generated = rule.generate(node);
            if (generated.isOk()) return generated;
            errors.add(generated);
        }

        if (errors.isEmpty()) {
            return new Err<>(new NodeException("No rules present", node));
        } else {
            return errors.get(errors.size() - 1).mapErr(err -> new NodeException("Failed to apply disjunction", node, err));
        }
    }
}
