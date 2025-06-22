package magma.rule;

import magma.error.CompileError;
import magma.error.NodeContext;
import magma.node.Node;
import magma.node.result.NodeResult;
import magma.string.StringErr;
import magma.string.StringResult;

public record TypeRule(String type, Rule<Node, StringResult> rule) implements Rule<Node, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        return this.rule.lex(input)
                .map(node -> node.retype(this.type));
    }

    @Override
    public StringResult generate(final Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);

        return new StringErr(new CompileError("Type '" + this.type + "' not present", new NodeContext(node)));
    }
}
