package magma.app.compile;

import magma.api.Err;
import magma.api.Result;

import java.util.ArrayList;
import java.util.List;

public record DisjunctionRule(List<Rule> rules) implements Rule {
    private Result<Node, CompileException> parse1(String input) {
        var errors = new ArrayList<Result<Node, CompileException>>();
        for (Rule rule : rules) {
            var parsed = rule.parse(input).result();
            if (parsed.isOk()) return parsed;
            errors.add(parsed);
        }

        if (errors.isEmpty()) {
            return new Err<>(new CompileException("No rules present", input));
        } else {
            return errors.get(errors.size() - 1).mapErr(err -> new CompileException("Failed to apply disjunction", input, err));
        }
    }

    private Result<String, CompileException> generate1(Node node) {
        var errors = new ArrayList<Result<String, CompileException>>();
        for (Rule rule : rules) {
            var generated = rule.generate(node).result();
            if (generated.isOk()) return generated;
            errors.add(generated);
        }

        if (errors.isEmpty()) {
            return new Err<>(new NodeException("No rules present", node));
        } else {
            return errors.get(errors.size() - 1).mapErr(err -> new NodeException("Failed to apply disjunction", node, err));
        }
    }

    @Override
    public CompileResult<Node> parse(String input) {
        return new CompileResult<>(parse1(input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return new CompileResult<>(generate1(node));
    }
}
