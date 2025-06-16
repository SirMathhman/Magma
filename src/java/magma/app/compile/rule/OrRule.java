package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.CompileError;
import magma.app.compile.context.StringContext;

import java.util.List;
import java.util.Optional;

public record OrRule<Node, R extends Rule<Node>>(List<R> rules) implements Rule<Node> {
    @Override
    public Result<Node, CompileError> lex(String input) {
        return this.rules.stream()
                .map(rule -> rule.lex(input)
                        .findValue())
                .flatMap(Optional::stream)
                .findFirst()
                .<Result<Node, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid rule", new StringContext(""))));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return this.rules.stream()
                .map(rule -> rule.generate(node)
                        .findValue())
                .flatMap(Optional::stream)
                .findFirst()
                .<Result<String, CompileError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new CompileError("Invalid rule", new StringContext(""))));
    }
}
