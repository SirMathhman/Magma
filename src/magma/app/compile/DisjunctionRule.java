package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.List;
import java.util.Optional;

public record DisjunctionRule(List<Rule> rules) implements Rule {
    private Optional<Node> parse0(String input) {
        for (Rule rule : rules) {
            var parsed = rule.parse(input).findValue();
            if (parsed.isPresent()) return parsed;
        }

        return Optional.empty();
    }

    private Optional<String> generate0(Node node) {
        for (Rule rule : rules) {
            var generated = rule.generate(node).findValue();
            if (generated.isPresent()) return generated;
        }

        return Optional.empty();
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse0(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate0(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new NodeException("Invalid node.", node)));
    }
}
