package magma.rule;

import magma.api.Tuple;
import magma.node.Node;
import magma.rule.locate.FirstLocator;
import magma.rule.locate.LastLocator;
import magma.rule.split.InfixSplitter;
import magma.rule.split.Splitter;

import java.util.Optional;

public final class SplitRule implements Rule {
    private final Rule leftRule;
    private final Rule rightRule;
    private final Splitter splitter;

    private SplitRule(final Rule leftRule, final Rule rightRule, final Splitter splitter) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.splitter = splitter;
    }

    public static Rule Last(final Rule leftRule, final String infix, final Rule rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new LastLocator()));
    }

    public static Rule First(final Rule leftRule, final String infix, final Rule rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new FirstLocator()));
    }

    @Override
    public Optional<Node> lex(final String input) {
        return this.splitter.split(input).flatMap(this::lexWithTuple);
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