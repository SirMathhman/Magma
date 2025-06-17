package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.context.NodeContext;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.FormattedError;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.TypedNode;

public final class TypeRule<Node extends DisplayNode & TypedNode<Node>> implements Rule

        <Node> {
    private final String type;
    private final Rule<Node> rule;

    public TypeRule(String type, Rule<Node> rule) {
        this.type = type;
        this.rule = rule;
    }

    @Override
    public Result<Node, FormattedError> lex(String input) {
        return this.rule.lex(input)
                .mapValue(result -> result.retype(this.type));
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        return new Err<>(new CompileError("Not of type '" + this.type + "'", new NodeContext(node)));
    }
}
