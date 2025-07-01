package magma.rule;

import magma.compile.result.StringResultFactory;
import magma.node.TypedNode;
import magma.node.result.MapNodeResult;

public final class TypeRule<Node extends TypedNode<Node>, Error, NodeResult extends MapNodeResult<NodeResult, Node, ResultFactory>, StringResult, ResultFactory extends StringResultFactory<Node, Error, StringResult>>
        implements Rule<Node, NodeResult, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final ResultFactory factory;

    public TypeRule(final String type, final Rule<Node, NodeResult, StringResult> rule, final ResultFactory factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(final String input) {
        return this.rule.lex(input)
                        .mapValue(node -> node.retype(this.type))
                        .mapErr("Type '" + this.type + "' cannot be assigned", input, this.factory);
    }

    @Override
    public StringResult generate(final Node node) {
        if (node.is(this.type)) return this.rule.generate(node);
        return this.factory.createStringError("Type '" + this.type + "' not present", node);
    }
}
