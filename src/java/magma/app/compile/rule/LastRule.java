package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.ResultCompileResultFactory;

public record LastRule<Node>(Rule<Node> leftRule, String infix, Rule<Node> rightRule) implements Rule<Node> {
    @Override
    public CompileResult<Node> lex(String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (separator < 0)
            return ResultCompileResultFactory.createResultCompileResultFactory()
                    .fromStringError("Invalid rule", "");

        final var rightResult = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightResult);
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return this.leftRule.generate(node)
                .flatMap(leftResult -> this.rightRule.generate(node)
                        .mapValue(rightResult -> leftResult + this.infix + rightResult));
    }
}