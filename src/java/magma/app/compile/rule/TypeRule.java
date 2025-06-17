package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.TypedNode;

public final class TypeRule<Node extends DisplayNode & TypedNode<Node>> implements Rule

        <Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    private final String type;
    private final Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule;
    private final ResultFactory<Node, FormattedError> factory;

    public TypeRule(String type, Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule, ResultFactory<Node, FormattedError> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
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
        return this.factory.fromNodeErr("Not of type '" + this.type + "'", node);
    }
}
