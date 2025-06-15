package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;
import magma.app.compile.node.NodeResults;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public StringResult<CompileError> generate(Node node) {
        return this.leftRule.generate(node)
                .appendString(this.infix)
                .appendMaybe(this.rightRule.generate(node));
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