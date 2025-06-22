package magma.rule;

import magma.error.CompileError;
import magma.error.NodeContext;
import magma.node.DisplayNode;
import magma.node.TypedNode;
import magma.node.result.NodeResult;
import magma.string.StringErr;
import magma.string.StringResult;

public final class TypeRule<Node extends TypedNode<Node> & DisplayNode> implements Rule<Node, StringResult> {
    private final String type;
    private final Rule<Node, StringResult> rule;

    public TypeRule(final String type, final Rule<Node, StringResult> rule) {
        this.type = type;
        this.rule = rule;
    }

    @Override
    public NodeResult<Node> lex(final String input) {
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
