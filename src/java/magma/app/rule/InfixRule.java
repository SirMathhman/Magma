package magma.app.rule;

import magma.app.CompileError;
import magma.app.Rule;
import magma.app.maybe.NodeResult;
import magma.app.maybe.node.ErrNodeResult;
import magma.app.maybe.string.Appendable;

public record InfixRule<Node, Generate extends Appendable<Generate>>(
        Rule<Node, NodeResult<Node, CompileError>, Generate> leftRule, String infix,
        Rule<Node, NodeResult<Node, CompileError>, Generate> rightRule) implements Rule<Node, NodeResult<Node, CompileError>, Generate> {

    @Override
    public Generate generate(Node node) {
        return this.leftRule.generate(node).appendString(this.infix).appendMaybe(this.rightRule.generate(node));
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0)
            return new ErrNodeResult<Node, CompileError>(new CompileError("Infix '" + this.infix + "' not present", new StringContext(input)));

        final var destination = input.substring(index + this.infix.length());
        return this.rightRule.lex(destination);
    }
}