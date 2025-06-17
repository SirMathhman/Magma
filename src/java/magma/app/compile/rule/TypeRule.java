package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.CompileErrors;
import magma.app.compile.error.FormattedError;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.TypedNode;

public final class TypeRule<Node extends DisplayNode & TypedNode<Node>> implements Rule

        <Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    private final String type;
    private final Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule;

    public TypeRule(String type, Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule) {
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
        return CompileErrors.fromNodeErr("Not of type '" + this.type + "'", node);
    }
}
