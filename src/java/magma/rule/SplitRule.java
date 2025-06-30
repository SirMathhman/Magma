package magma.rule;

import magma.api.Tuple;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.rule.locate.FirstLocator;
import magma.rule.locate.LastLocator;
import magma.rule.split.InfixSplitter;
import magma.rule.split.Splitter;
import magma.string.result.StringResult;

public final class SplitRule implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> {
    private final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> leftRule;
    private final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> rightRule;
    private final Splitter splitter;

    private SplitRule(final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> leftRule,
                      final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> rightRule,
                      final Splitter splitter) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.splitter = splitter;
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> Last(final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> leftRule,
                                                                                                   final String infix,
                                                                                                   final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new LastLocator()));
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> First(final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> leftRule,
                                                                                                    final String infix,
                                                                                                    final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> rightRule) {
        return new SplitRule(leftRule, rightRule, new InfixSplitter(infix, new FirstLocator()));
    }

    private NodeResult<EverythingNode> lexWithTuple(final Tuple<String, String> tuple) {
        final var leftSlice = tuple.left();
        final var rightSlice = tuple.right();

        return this.leftRule.lex(leftSlice).flatMap(leftResult -> {
            final var rightResult = this.rightRule.lex(rightSlice);
            return rightResult.map(leftResult::merge);
        });
    }

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        return this.splitter.split(input)
                            .map(this::lexWithTuple)
                            .orElseGet(() -> NodeErr.create(this.splitter.createMessage(), input));
    }

    @Override
    public StringResult<FormatError> generate(final EverythingNode node) {
        return this.leftRule.generate(node).flatMap(leftResult -> {
            final var generated = this.rightRule.generate(node);
            return generated.map(rightResult -> this.splitter.join(leftResult, rightResult));
        });
    }
}