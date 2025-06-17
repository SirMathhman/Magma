package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.TypedNode;

public final class TypeRule<Node extends DisplayNode & TypedNode<Node>, Error> implements Rule<Node, Result<Node, Error>, Result<String, Error>> {
    private final String type;
    private final Rule<Node, Result<Node, Error>, Result<String, Error>> rule;
    private final ResultFactory<Node, Result<Node, Error>, Result<String, Error>> factory;

    public TypeRule(String type, Rule<Node, Result<Node, Error>, Result<String, Error>> rule, ResultFactory<Node, Result<Node, Error>, Result<String, Error>> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public Result<Node, Error> lex(String input) {
        return this.rule.lex(input)
                .mapValue(result -> result.retype(this.type));
    }

    @Override
    public Result<String, Error> generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        return this.factory.fromNodeErr("Not of type '" + this.type + "'", node);
    }
}
