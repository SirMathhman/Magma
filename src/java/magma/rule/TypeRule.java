package magma.rule;

import magma.compile.result.ResultFactory;
import magma.error.FormatError;
import magma.node.TypedNode;
import magma.node.result.NodeResult;

public final class TypeRule<Node extends TypedNode<Node>, StringResult>
        implements Rule<Node, NodeResult<Node, FormatError>, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult<Node, FormatError>, StringResult> rule;
    private final ResultFactory<Node, FormatError, StringResult> factory;

    public TypeRule(final String type,
                    final Rule<Node, NodeResult<Node, FormatError>, StringResult> rule,
                    final ResultFactory<Node, FormatError, StringResult> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node, FormatError> lex(final String input) {
        return this.rule.lex(input).mapValue(node -> node.retype(this.type)).mapErr("Type '" + this.type + "' cannot be assigned", input);
    }

    @Override
    public StringResult generate(final Node node) {
        if (node.is(this.type)) return this.rule.generate(node);
        return this.factory.createStringError("Type '" + this.type + "' not present", node);
    }
}
