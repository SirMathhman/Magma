package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.ResultCompileResult;

import java.util.List;
import java.util.Optional;

public record OrRule<Node, R extends Rule<Node>>(List<R> rules) implements Rule<Node> {
    @Override
    public CompileResult<Node> lex(String input) {
        return this.rules.stream()
                .map(rule -> rule.lex(input)
                        .result()
                        .findValue())
                .flatMap(Optional::stream)
                .findFirst()
                .map(ResultCompileResult::fromValue)
                .orElseGet(() -> ResultCompileResult.fromStringError("Invalid combination", ""));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return this.rules.stream()
                .map(rule -> rule.generate(node)
                        .result()
                        .findValue())
                .flatMap(Optional::stream)
                .findFirst()
                .map(ResultCompileResult::fromValue)
                .orElseGet(() -> ResultCompileResult.fromStringError("Invalid combination", ""));
    }
}
