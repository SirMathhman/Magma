package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.DisplayNode;
import magma.app.compile.node.TypedNode;

import java.util.function.Function;

public final class TypeRule<Node extends DisplayNode & TypedNode<Node>, Error, StringResult> implements Rule<Node, Result<Node, Error>, StringResult> {
    private final String type;
    private final Rule<Node, Result<Node, Error>, StringResult> rule;
    private final ResultFactory<Node, Result<Node, Error>, StringResult> factory;

    public TypeRule(String type, Rule<Node, Result<Node, Error>, StringResult> rule, ResultFactory<Node, Result<Node, Error>, StringResult> factory) {
        this.type = type;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public Result<Node, Error> lex(String input) {
        Result<Node, Error> nodeErrorResult = this.rule.lex(input);
        return switch (nodeErrorResult) {
            case Err<Node, Error>(Error error) -> new Err<>(error);
            case Ok<Node, Error>(
                    Node value
            ) -> new Ok<>(((Function<Node, Node>) result -> result.retype(this.type)).apply(value));
        };
    }

    @Override
    public StringResult generate(Node node) {
        if (node.is(this.type))
            return this.rule.generate(node);
        return this.factory.fromNodeErr("Not of type '" + this.type + "'", node);
    }
}
