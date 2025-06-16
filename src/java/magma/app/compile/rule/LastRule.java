package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Result;
import magma.app.compile.CompileError;
import magma.app.compile.context.StringContext;

public record LastRule<Node>(Rule<Node> leftRule, String infix, Rule<Node> rightRule) implements Rule<Node> {
    @Override
    public Result<Node, CompileError> lex(String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (separator < 0)
            return new Err<>(new CompileError("Invalid rule", new StringContext("")));

        final var rightResult = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightResult);
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return this.leftRule.generate(node)
                .flatMap(leftResult -> this.rightRule.generate(node)
                        .map(rightResult -> leftResult + this.infix + rightResult));
    }
}