package magma.app.compile.rule;

import magma.app.compile.error.NodeResult;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.error.StringResult;
import magma.app.compile.node.Node;

public final class InfixRule implements Rule<NodeResult, StringResult> {
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
        return this.leftRule.lex(left)
                .merge(() -> this.rightRule.lex(right));
    }

    @Override
    public StringResult generate(Node node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .appendResult(() -> this.rightRule.generate(node));
    }
}
