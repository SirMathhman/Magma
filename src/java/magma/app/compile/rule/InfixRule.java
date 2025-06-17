package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.node.MergingNode;

public final class InfixRule<Node extends MergingNode<Node>, Error> implements Rule<Node, Result<Node, Error>, Result<String, Error>> {
    private final Rule<Node, Result<Node, Error>, Result<String, Error>> leftRule;
    private final String infix;
    private final Rule<Node, Result<Node, Error>, Result<String, Error>> rightRule;
    private final ResultFactory<Node, Result<Node, Error>, Result<String, Error>> factory;

    public InfixRule(Rule<Node, Result<Node, Error>, Result<String, Error>> leftRule, String infix, Rule<Node, Result<Node, Error>, Result<String, Error>> rightRule, ResultFactory<Node, Result<Node, Error>, Result<String, Error>> factory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.factory = factory;
    }

    @Override
    public Result<Node, Error> lex(String input) {
        final var index = input.indexOf(this.infix);
        if (index == -1)
            return this.factory.fromStringErr("Infix '" + this.infix + "' not present", input);

        final var left = input.substring(0, index);
        final var right = input.substring(index + this.infix.length());
        Result<Node, Error> nodeErrorResult1 = this.leftRule.lex(left);
        return switch (nodeErrorResult1) {
            case Err<Node, Error>(Error error1) -> new Err<>(error1);
            case Ok<Node, Error>(
                    Node value1
            ) -> {
                Result<Node, Error> nodeErrorResult = this.rightRule.lex(right);
                yield this.getNodeErrorResult(value1, nodeErrorResult);
            }
        };
    }

    private Result<Node, Error> getNodeErrorResult(Node value1, Result<Node, Error> nodeErrorResult) {
        return switch (nodeErrorResult) {
            case Err<Node, Error>(Error error) -> new Err<>(error);
            case Ok<Node, Error>(
                    Node value
            ) -> new Ok<>(value1.merge(value));
        };
    }

    @Override
    public Result<String, Error> generate(Node node) {
        Result<String, Error> stringErrorResult1 = this.leftRule.generate(node);
        return switch (stringErrorResult1) {
            case Err<String, Error>(Error error1) -> new Err<>(error1);
            case Ok<String, Error>(
                    String value1
            ) -> {
                Result<String, Error> stringErrorResult = this.rightRule.generate(node);
                yield this.getStringErrorResult(value1, stringErrorResult);
            }
        };
    }

    private Result<String, Error> getStringErrorResult(String value1, Result<String, Error> stringErrorResult) {
        return switch (stringErrorResult) {
            case Err<String, Error>(Error error) -> new Err<>(error);
            case Ok<String, Error>(String value) -> new Ok<>(value1 + this.infix + value);
        };
    }
}
