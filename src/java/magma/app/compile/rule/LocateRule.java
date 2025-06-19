package magma.app.compile.rule;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.attribute.MergingNode;
import magma.app.compile.rule.action.CompileResults;
import magma.app.compile.rule.locate.FirstLocator;
import magma.app.compile.rule.locate.LastLocator;
import magma.app.compile.rule.locate.Locator;

import java.util.Optional;

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

    private Optional<String> generate0(Node node) {
        return Optional.of(this.leftRule.generate(node)
                .findValue()
                .orElse("") + this.infix + this.rightRule.generate(node)
                .findValue()
                .orElse(""));
    }

    private Optional<Node> lex0(String input) {
        final var maybeIndex = this.locator.locate(input, this.infix);
        if (maybeIndex.isEmpty())
            return Optional.empty();

        final int index = maybeIndex.get();
        final var leftSlice = input.substring(0, index);
        final var rightSlice = input.substring(index + this.infix.length());
        return (this.leftRule).lex(leftSlice)
                .findValue()
                .flatMap(leftResult -> (this.rightRule).lex(rightSlice)
                        .findValue()
                        .map(leftResult::merge));
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return CompileResults.fromOptionWithString(this.lex0(input), input);
    }

    @Override
    public StringResult generate(Node node) {
        return CompileResults.fromOptionWithNode(this.generate0(node), node);
    }
}