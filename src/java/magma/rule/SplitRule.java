package magma.rule;

import magma.api.Tuple;
import magma.node.EverythingNode;
import magma.rule.locate.FirstLocator;
import magma.rule.locate.LastLocator;
import magma.rule.split.InfixSplitter;
import magma.rule.split.Splitter;

import java.util.Optional;

public final class SplitRule implements Rule<EverythingNode> {
    private final Rule<EverythingNode> leftRule;
    private final Rule<EverythingNode> rightRule;
    private final Splitter splitter;

    private SplitRule(final Rule<EverythingNode> leftRule, final Rule<EverythingNode> rightRule, final Splitter splitter) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.splitter = splitter;
    }

    public static Rule<EverythingNode> Last(final Rule<EverythingNode> leftRule, final String infix, final Rule<EverythingNode> rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new LastLocator()));
    }

    public static Rule<EverythingNode> First(final Rule<EverythingNode> leftRule, final String infix, final Rule<EverythingNode> rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new FirstLocator()));
    }

    private Optional<EverythingNode> lex0(final String input) {
        return this.splitter.split(input).flatMap(this::lexWithTuple);
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.leftRule.generate(node).flatMap(leftResult -> {
            final var generated = this.rightRule.generate(node);
            return generated.map(rightResult -> this.splitter.join(leftResult, rightResult));
        });
    }

    private Optional<EverythingNode> lexWithTuple(final Tuple<String, String> tuple) {
        final var leftSlice = tuple.left();
        final var rightSlice = tuple.right();

        return this.leftRule.lex(leftSlice).toOptional().flatMap(leftResult -> {
            final var rightResult = this.rightRule.lex(rightSlice).toOptional();
            return rightResult.map(leftResult::merge);
        });
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.lex0(input).<NodeResult<EverythingNode>>map(NodeOk::new).orElseGet(() -> new NodeErr<>());
    }
}