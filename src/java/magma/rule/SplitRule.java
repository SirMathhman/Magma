package magma.rule;

import magma.api.Tuple;
import magma.node.Node;
import magma.rule.locate.FirstLocator;
import magma.rule.locate.LastLocator;
import magma.rule.split.InfixSplitter;
import magma.rule.split.Splitter;

import java.util.Optional;

public final class SplitRule implements Rule<Node> {
    private final Rule<Node> leftRule;
    private final Rule<Node> rightRule;
    private final Splitter splitter;

    private SplitRule(final Rule<Node> leftRule, final Rule<Node> rightRule, final Splitter splitter) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.splitter = splitter;
    }

    public static Rule<Node> Last(final Rule<Node> leftRule, final String infix, final Rule<Node> rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new LastLocator()));
    }

    public static Rule<Node> First(final Rule<Node> leftRule, final String infix, final Rule<Node> rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new FirstLocator()));
    }

    @Override
    public Optional<Node> lex(final String input) {
        return this.splitter.split(input).flatMap(this::lexWithTuple);
    }

    @Override
    public Optional<String> generate(final Node node) {
        return this.leftRule.generate(node).flatMap(leftResult -> {
            final var generated = this.rightRule.generate(node);
            return generated.map(rightResult -> this.splitter.join(leftResult, rightResult));
        });
    }

    private Optional<Node> lexWithTuple(final Tuple<String, String> tuple) {
        final var leftSlice = tuple.left();
        final var rightSlice = tuple.right();

        return this.leftRule.lex(leftSlice).flatMap(leftResult -> {
            final var rightResult = this.rightRule.lex(rightSlice);
            return rightResult.map(leftResult::merge);
        });
    }

}