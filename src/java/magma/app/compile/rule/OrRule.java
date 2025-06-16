package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.CompileResultFactory;

import java.util.List;
import java.util.Optional;

public final class OrRule<Node, R extends Rule<Node>> implements Rule<Node> {
    private final List<R> rules;
    private final CompileResultFactory<Node, CompileResult<String>, CompileResult<Node>, CompileResult<List<Node>>> resultFactory;

    public OrRule(List<R> rules, CompileResultFactory<Node, CompileResult<String>, CompileResult<Node>, CompileResult<List<Node>>> resultFactory) {
        this.rules = rules;
        this.resultFactory = resultFactory;
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return this.rules.stream()
                .map(rule -> rule.lex(input)
                        .result()
                        .findValue())
                .flatMap(Optional::stream)
                .findFirst()
                .map(this.resultFactory::fromNode)
                .orElseGet(() -> this.resultFactory.fromStringError("Invalid combination", ""));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return this.rules.stream()
                .map(rule -> rule.generate(node)
                        .result()
                        .findValue())
                .flatMap(Optional::stream)
                .findFirst()
                .map(this.resultFactory::fromString)
                .orElseGet(() -> this.resultFactory.fromNodeError("Invalid combination", node));
    }
}
