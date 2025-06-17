package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactoryImpl;
import magma.app.compile.node.MergingNode;

public record InfixRule<Node extends MergingNode<Node>>(
        Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> leftRule, String infix,
        Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rightRule) implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    @Override
    public Result<Node, FormattedError> lex(String input) {
        final var index = input.indexOf(this.infix);
        if (index == -1)
            return ResultFactoryImpl.create()
                    .fromStringErr("Infix '" + this.infix + "' not present", input);

        final var left = input.substring(0, index);
        final var right = input.substring(index + this.infix.length());
        return this.leftRule.lex(left)
                .flatMapValue(leftResult -> this.rightRule.lex(right)
                        .mapValue(leftResult::merge));
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.leftRule.generate(node)
                .flatMapValue(leftResult -> this.rightRule.generate(node)
                        .mapValue(rightResult -> leftResult + this.infix + rightResult));
    }
}
