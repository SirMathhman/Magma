package magma.app.compile.rule;

import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.TypedNode;
import magma.app.compile.node.result.NodeResult;

public final class TypeRule<Node extends TypedNode<Node> & DisplayNode, Error, ErrorSequence, StringResult> implements
        Rule<Node, NodeResult<Node, Error>, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult<Node, Error>, StringResult> rule;
    private final ResultFactory<Node, NodeResult<Node, Error>, StringResult, ErrorSequence> factory;

    public TypeRule(final String type, final Rule<Node, NodeResult<Node, Error>, StringResult> rule, final ResultFactory<Node, NodeResult<Node, Error>, StringResult, ErrorSequence> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node, Error> lex(final String input) {
        return rule.lex(input)
                .map(node -> node.retype(type));
    }

    @Override
    public StringResult generate(final Node node) {
        if (node.is(type))
            return rule.generate(node);

        return factory.fromStringError("Type '" + type + "' not present", node);
    }
}
