package magma.rule;

import magma.api.Tuple;
import magma.compile.result.NodeResultFactory;
import magma.node.MergingNode;
import magma.node.result.MapNodeResult;
import magma.rule.split.Splitter;
import magma.string.result.MappingStringResult;

public final class SplitRule<Node extends MergingNode<Node>, Error, StringResult extends MappingStringResult<StringResult>, NodeResult extends MapNodeResult<NodeResult, Node, ResultFactory>, ResultFactory extends NodeResultFactory<Node, Error, NodeResult>>
        implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> leftRule;
    private final Rule<Node, NodeResult, StringResult> rightRule;
    private final Splitter splitter;
    private final ResultFactory factory;

    public SplitRule(final Rule<Node, NodeResult, StringResult> leftRule,
                     final Rule<Node, NodeResult, StringResult> rightRule,
                     final Splitter splitter,
                     final ResultFactory factory) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
        this.splitter = splitter;
        this.factory = factory;
    }

    private NodeResult lexWithTuple(final Tuple<String, String> tuple) {
        final var leftSlice = tuple.left();
        final var rightSlice = tuple.right();

        return this.leftRule.lex(leftSlice).flatMap(leftResult -> {
            final var rightResult = this.rightRule.lex(rightSlice);
            return rightResult.mapValue(leftResult::merge);
        });
    }

    @Override
    public NodeResult lex(final String input) {
        return this.splitter.split(input).map(this::lexWithTuple).orElseGet(() -> {
            final String message = this.splitter.createMessage();
            return this.factory.createNodeError(message, input);
        });
    }

    @Override
    public StringResult generate(final Node node) {
        return this.leftRule.generate(node).flatMap(leftResult -> {
            final var generated = this.rightRule.generate(node);
            return generated.map(rightResult -> this.splitter.join(leftResult, rightResult));
        });
    }
}