package magma.app.compile.rule;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.attribute.MergingNode;
import magma.app.compile.rule.action.CompileResults;
import magma.app.compile.rule.locate.FirstLocator;
import magma.app.compile.rule.locate.LastLocator;
import magma.app.compile.rule.locate.Locator;

public final class LocateRule<Node extends MergingNode<Node>> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final Rule<Node, NodeResult<Node>, StringResult> leftRule;
    private final String infix;
    private final Rule<Node, NodeResult<Node>, StringResult> rightRule;
    private final Locator locator;

    public LocateRule(Rule<Node, NodeResult<Node>, StringResult> leftRule, String infix, Rule<Node, NodeResult<Node>, StringResult> rightRule, Locator locator) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.locator = locator;
    }

    public static <Node extends MergingNode<Node>> Rule<Node, NodeResult<Node>, StringResult> Last(Rule<Node, NodeResult<Node>, StringResult> leftRule, String infix, Rule<Node, NodeResult<Node>, StringResult> rightRule) {
        return new LocateRule<>(leftRule, infix, rightRule, new LastLocator());
    }

    public static <Node extends MergingNode<Node>> Rule<Node, NodeResult<Node>, StringResult> First(Rule<Node, NodeResult<Node>, StringResult> leftRule, String infix, Rule<Node, NodeResult<Node>, StringResult> rightRule) {
        return new LocateRule<>(leftRule, infix, rightRule, new FirstLocator());
    }

    @Override
    public NodeResult<Node> lex(String input) {
        final var maybeIndex = this.locator.locate(input, this.infix);
        if (maybeIndex.isEmpty())
            return CompileResults.fromNodeError("Infix '" + this.infix + "' not present", input);

        final int index = maybeIndex.get();
        final var leftSlice = input.substring(0, index);
        final var rightSlice = input.substring(index + this.infix.length());
        return this.leftRule.lex(leftSlice)
                .mergeResult(() -> this.rightRule.lex(rightSlice), MergingNode::merge);
    }

    @Override
    public StringResult generate(Node node) {
        final var leftResult = this.leftRule.generate(node);
        return leftResult.appendSlice(this.infix)
                .appendResult(() -> this.rightRule.generate(node));
    }
}