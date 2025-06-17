package magma.app.rule;

import magma.CompileError;
import magma.StringContext;
import magma.api.Err;
import magma.api.Result;
import magma.app.node.Node;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public Result<Node, CompileError> lex(String input) {
        final var index = input.indexOf(this.infix);
        if (index == -1)
            return new Err<>(new CompileError("Infix '" + this.infix + "' not present", new StringContext(input)));

        final var left = input.substring(0, index);
        final var right = input.substring(index + this.infix.length());
        return this.leftRule.lex(left)
                .flatMap(leftResult -> this.rightRule.lex(right)
                        .mapValue(leftResult::merge));
    }

    @Override
    public Result<String, CompileError> generate(Node node) {
        return this.leftRule.generate(node)
                .flatMap(leftResult -> this.rightRule.generate(node)
                        .mapValue(rightResult -> leftResult + this.infix + rightResult));
    }
}
