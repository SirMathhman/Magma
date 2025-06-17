package magma.app.compile.rule;

import magma.app.compile.error.NodeErr;
import magma.app.compile.error.NodeOk;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.Node;

public final class TypeRule implements Rule<Node, NodeResult, StringResult> {
    private final String type;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final ResultFactory<Node, NodeResult, StringResult> factory;

    public TypeRule(String type, Rule<Node, NodeResult, StringResult> rule, ResultFactory<Node, NodeResult, StringResult> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        NodeResult nodeErrorResult = this.rule.lex(input);
        return switch (nodeErrorResult) {
            case NodeErr(var error) -> new NodeErr(error);
            case NodeOk(var value) -> new NodeOk(value.retype(this.type));
        };
    }

    @Override
    public StringResult generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        return this.factory.fromNodeErr("Not of type '" + this.type + "'", node);
    }
}
