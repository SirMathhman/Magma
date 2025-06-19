package magma.app.compile.rule;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.attribute.NodeWithType;
import magma.app.compile.rule.action.CompileResults;

public final class TypeRule<Node extends NodeWithType<Node>> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult<Node>, StringResult> rule;

    public TypeRule(String type, Rule<Node, NodeResult<Node>, StringResult> rule) {
        this.type = type;
        this.rule = rule;
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return this.rule.lex(input)
                .map(node -> node.retype(this.type));
    }

    @Override
    public StringResult generate(Node node) {
        return node.is(this.type) ? this.rule.generate(node) : CompileResults.fromStringError(node);
    }
}
