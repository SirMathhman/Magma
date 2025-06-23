package magma.app.compile.rule;

import magma.app.compile.node.property.DisplayNode;
import magma.app.compile.node.property.TypedNode;
import magma.app.compile.node.result.Mapping;
import magma.app.compile.string.ParentStringResultFactory;

public final class TypeRule<Node extends TypedNode<Node> & DisplayNode, Error, NodeResult extends Mapping<Node, NodeResult>, StringResult, Factory extends ParentStringResultFactory<Node, StringResult, Error>> implements
        Rule<Node, NodeResult, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final Factory factory;

    public TypeRule(final String type, final Rule<Node, NodeResult, StringResult> rule, final Factory factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(final String input) {
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