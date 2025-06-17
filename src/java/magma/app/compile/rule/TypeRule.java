package magma.app.compile.rule;

import magma.app.compile.FormattedError;
import magma.app.compile.ResultFactory;
import magma.app.compile.TypeNodeResult;
import magma.app.compile.node.TypeNode;

public final class TypeRule<Node extends TypeNode<Node>, NodeResult extends TypeNodeResult<NodeResult>, StringResult> implements Rule<Node, NodeResult, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final ResultFactory<Node, FormattedError, NodeResult, StringResult> factory;

    public TypeRule(String type, Rule<Node, NodeResult, StringResult> rule, ResultFactory<Node, FormattedError, NodeResult, StringResult> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        return this.rule.lex(input)
                .retype(this.type);
    }

    @Override
    public StringResult generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        return this.factory.fromNodeErr("Not of type '" + this.type + "'", node);
    }
}
