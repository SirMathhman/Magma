package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompileException;

import java.util.Optional;

public record TypeRule(String type, Rule child) implements Rule {
    private Optional<Node> parse1(String input) {
        return child.parse(input).findValue().map(node -> node.retype(type));
    }

    @Override
    public Result<Node, CompileException> parse(String input) {
        return parse1(input)
                .<Result<Node, CompileException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileException("Invalid input", input)));
    }

    @Override
    public Result<String, CompileException> generate(Node node) {
        return node.is(type)
                ? child.generate(node)
                : new Err<>(new CompileException("Type '%s' not present".formatted(type), node.toString()));
    }
}
