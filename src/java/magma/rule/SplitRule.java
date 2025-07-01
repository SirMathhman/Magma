package magma.rule;

import magma.api.Tuple;
import magma.compile.result.ResultFactory;
import magma.error.FormatError;
import magma.node.MergingNode;
import magma.node.result.NodeResult;
import magma.rule.split.Splitter;
import magma.string.result.StringResult;

public final class SplitRule<Node extends MergingNode<Node>>
        implements Rule<Node, NodeResult<Node>, StringResult<FormatError>> {
    private final Rule<Node, NodeResult<Node>, StringResult<FormatError>> leftRule;
    private final Rule<Node, NodeResult<Node>, StringResult<FormatError>> rightRule;
    private final Splitter splitter;
    private final ResultFactory<Node, StringResult<FormatError>> factory;

    public SplitRule(final Rule<Node, NodeResult<Node>, StringResult<FormatError>> leftRule,
                     final Rule<Node, NodeResult<Node>, StringResult<FormatError>> rightRule,
                     final Splitter splitter,
                     final ResultFactory<Node, StringResult<FormatError>> factory) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.splitter = splitter;
        this.factory = factory;
    }

    private NodeResult<Node> lexWithTuple(final Tuple<String, String> tuple) {
        final var leftSlice = tuple.left();
        final var rightSlice = tuple.right();

        return this.leftRule.lex(leftSlice).flatMap(leftResult -> {
            final var rightResult = this.rightRule.lex(rightSlice);
            return rightResult.mapValue(leftResult::merge);
        });
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        return this.splitter.split(input).map(this::lexWithTuple).orElseGet(() -> {
            final String message = this.splitter.createMessage();
            return this.factory.createNodeError(message, input);
        });
    }

    @Override
    public StringResult<FormatError> generate(final Node node) {
        return this.leftRule.generate(node).flatMap(leftResult -> {
            final var generated = this.rightRule.generate(node);
            return generated.map(rightResult -> this.splitter.join(leftResult, rightResult));
        });
    }
}