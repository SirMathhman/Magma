package magma.rule;

import magma.compile.result.ResultFactory;
import magma.error.FormatError;
import magma.node.TypedNode;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public record TypeRule<Node extends TypedNode<Node>>(String type, Rule<Node, NodeResult<Node>, StringResult<FormatError>> rule)
        implements Rule<Node, NodeResult<Node>, StringResult<FormatError>> {

    @Override
    public NodeResult<Node> lex(final String input) {
        return this.rule.lex(input).map(node -> node.retype(this.type));
    }

    @Override
    public StringResult<FormatError> generate(final Node node) {
        if (node.is(this.type)) return this.rule.generate(node);
        return ResultFactory.create("Type '" + this.type + "' not present", node);
    }
}
