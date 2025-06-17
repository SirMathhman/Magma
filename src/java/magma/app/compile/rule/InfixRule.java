package magma.app.compile.rule;

import magma.app.compile.error.NodeErr;
import magma.app.compile.error.NodeOk;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.error.StringErr;
import magma.app.compile.error.StringOk;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.Node;

public final class InfixRule<Error> implements Rule<NodeResult, StringResult> {
    private final Rule<NodeResult, StringResult> leftRule;
    private final String infix;
    private final Rule<NodeResult, StringResult> rightRule;
    private final ResultFactory<Node, NodeResult, StringResult> factory;

    public InfixRule(Rule<NodeResult, StringResult> leftRule, String infix, Rule<NodeResult, StringResult> rightRule, ResultFactory<Node, NodeResult, StringResult> factory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        final var index = input.indexOf(this.infix);
        if (index == -1)
            return this.factory.fromStringErr("Infix '" + this.infix + "' not present", input);

        final var left = input.substring(0, index);
        final var right = input.substring(index + this.infix.length());
        NodeResult nodeErrorResult1 = this.leftRule.lex(left);
        return switch (nodeErrorResult1) {
            case NodeErr(var error1) -> new NodeErr(error1);
            case NodeOk(var value1) -> {
                NodeResult nodeErrorResult = this.rightRule.lex(right);
                yield this.getNodeErrorResult(value1, nodeErrorResult);
            }
        };
    }

    private NodeResult getNodeErrorResult(Node value1, NodeResult nodeErrorResult) {
        return switch (nodeErrorResult) {
            case NodeErr(var error) -> new NodeErr(error);
            case NodeOk(
                    var value
            ) -> new NodeOk(value1.merge(value));
        };
    }

    @Override
    public StringResult generate(Node node) {
        StringResult stringErrorResult1 = this.leftRule.generate(node);
        return switch (stringErrorResult1) {
            case StringErr(var error1) -> new StringErr(error1);
            case StringOk(
                    String value1
            ) -> {
                StringResult stringErrorResult = this.rightRule.generate(node);
                yield this.getStringErrorResult(value1, stringErrorResult);
            }
        };
    }

    private StringResult getStringErrorResult(String value1, StringResult stringErrorResult) {
        return switch (stringErrorResult) {
            case StringErr(var error) -> new StringErr(error);
            case StringOk(var value) -> new StringOk(value1 + this.infix + value);
        };
    }
}
