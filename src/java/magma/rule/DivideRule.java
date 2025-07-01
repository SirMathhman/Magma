package magma.rule;

import magma.api.Tuple;
import magma.compile.result.NodeResultFactory;
import magma.compile.result.StringResultFactory;
import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.node.NodeWithNodeLists;
import magma.node.result.NodeListResult;
import magma.string.result.ConcatStringResult;

import java.util.Collections;
import java.util.stream.Stream;

public final class DivideRule<Node extends NodeWithNodeLists<Node>, Error, StringResult extends ConcatStringResult<StringResult>, NodeResult, ResultFactory extends NodeResultFactory<Node, Error, NodeResult> & StringResultFactory<Node, Error, StringResult>>
        implements Rule<Node, NodeResult, StringResult> {
    private final String key;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final ResultFactory resultFactory;

    public DivideRule(final String key,
                      final Rule<Node, NodeResult, StringResult> rule,
                      final ResultFactory resultFactory) {
        this.key = key;
        this.rule = rule;
        this.resultFactory = resultFactory;
    }

    private static Stream<String> divide(final CharSequence input) {
        var current = new Tuple<>(true, (DivideState) new MutableDivideState(input));
        while (current.left()) {
            final var right = current.right();
            current = DivideRule.fold(right);
        }

        return current.right().advance().stream();
    }

    private static Tuple<Boolean, DivideState> fold(final DivideState state) {
        final var maybeNextTuple = state.pop();
        if (maybeNextTuple.isEmpty()) return new Tuple<>(false, state);

        final var nextTuple = maybeNextTuple.get();
        final var nextState = nextTuple.left();
        final var next = nextTuple.right();

        final var folded = DivideRule.fold(nextState, next);
        return new Tuple<>(true, folded);
    }

    private static DivideState fold(final DivideState current, final char next) {
        final var appended = current.append(next);
        if (';' == next && appended.isLevel()) return appended.advance();
        if ('{' == next) return appended.enter();
        if ('}' == next) return appended.exit();
        return appended;
    }

    @Override
    public NodeResult lex(final String input) {
        return DivideRule.divide(input)
                         .map(this.rule::lex)
                         .reduce(this.resultFactory.createNodeList(), NodeListResult::add, (_, next) -> next)
                         .toNode(this.key);
    }

    @Override
    public StringResult generate(final Node node) {
        return node.findNodeList(this.key)
                   .orElse(Collections.emptyList())
                   .stream()
                   .map(this.rule::generate)
                   .reduce(this.resultFactory.createEmptyString(), ConcatStringResult::appendResult, (_, next) -> next);
    }
}