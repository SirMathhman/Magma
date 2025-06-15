package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Rule;
import magma.app.compile.NodeResult;
import magma.app.compile.node.NodeResults;
import magma.app.compile.string.Appending;

public record InfixRule<Node, Generate extends Appending<Generate>>(
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
            return NodeResults.createFromString("Infix '" + this.infix + "' not present", input);

        final var destination = input.substring(index + this.infix.length());
        return this.rightRule.lex(destination);
    }
}