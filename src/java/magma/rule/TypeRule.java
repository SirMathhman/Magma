package magma.rule;

import magma.compile.result.ResultFactory;
import magma.node.TypedNode;
import magma.node.result.NodeResult;

public final class TypeRule<Node extends TypedNode<Node>, StringResult>
        implements Rule<Node, NodeResult<Node>, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult<Node>, StringResult> rule;
    private final ResultFactory<StringResult> factory;

    public TypeRule(final String type,
                    final Rule<Node, NodeResult<Node>, StringResult> rule,
                    final ResultFactory<StringResult> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        return this.rule.lex(input).map(node -> node.retype(this.type));
    }

    @Override
    public StringResult generate(final Node node) {
        if (node.is(this.type)) return this.rule.generate(node);
        return this.factory.create("Type '" + this.type + "' not present", node);
    }
}
