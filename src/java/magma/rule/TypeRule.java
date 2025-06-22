package magma.rule;

import magma.factory.ResultFactory;
import magma.node.DisplayNode;
import magma.node.TypedNode;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class TypeRule<Node extends TypedNode<Node> & DisplayNode, Error> implements Rule<Node, NodeResult<Node, Error>, StringResult<Error>> {
    private final String type;
    private final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule;
    private final ResultFactory<Node, Error, NodeResult<Node, Error>, StringResult<Error>> factory;

    public TypeRule(final String type, final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule, final ResultFactory<Node, Error, NodeResult<Node, Error>, StringResult<Error>> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node, Error> lex(final String input) {
        return this.rule.lex(input)
                .map(node -> node.retype(this.type));
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);

        return this.factory.fromStringError("Type '" + this.type + "' not present", node);
    }
}
