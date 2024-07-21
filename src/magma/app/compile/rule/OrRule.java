package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record OrRule(List<Rule> children) implements Rule {
    private Optional<Map<String, String>> parse0(String input) {
        for (Rule child : children) {
            var result = child.parse(input).findValue().map(Node::strings);
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }

    private Optional<String> generate0(Map<String, String> node) {
        for (Rule child : children) {
            var result = child.generate(new Node(Optional.empty(), node)).findValue();
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }

    private Optional<Node> parse1(String input) {
        return parse0(input).map(strings -> new Node(Optional.empty(), strings));
    }

    private Optional<String> generate1(Node node) {
        return generate0(node.strings());
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse1(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return generate1(node)
                .<Result<String, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Cannot generate", node.toString())));
    }
}
