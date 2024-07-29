package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record DisjunctionRule(List<Rule> rules) implements Rule {

    private Optional<String> generate0(Node node) {
        for (Rule rule : rules) {
            var generated = rule.generate(node).findValue();
            if (generated.isPresent()) return generated;
        }

        return Optional.empty();
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        var errors = new ArrayList<Result<Node, CompileException>>();
        for (Rule rule : rules) {
            var parsed = rule.parse(input);
            if (parsed.isOk()) return parsed;
            errors.add(parsed);
        }

        if(errors.isEmpty()) {
            return new Err<>(new CompileException("No rules present", input));
        } else {
            return errors.get(errors.size() - 1).mapErr(err -> new CompileException("Failed to apply disjunction", input, err));
        }
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate0(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new NodeException("Invalid node.", node)));
    }
}
