package magma.rule;

import magma.api.Tuple;
import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.node.EverythingNode;
import magma.node.result.NodeListOk;
import magma.node.result.NodeListResult;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

import java.util.Collections;
import java.util.stream.Stream;

public record DivideRule(String key, Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rule)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
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
    public NodeResult<EverythingNode> lex(final String input) {
        return DivideRule.divide(input)
                         .map(this.rule::lex)
                         .reduce(new NodeListOk(), NodeListResult::add, (_, next) -> next)
                         .toNode(this.key);
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return node.findNodeList(this.key)
                   .orElse(Collections.emptyList())
                   .stream()
                   .map(this.rule::generate)
                   .reduce(new StringOk(), StringResult::appendResult, (_, next) -> next);
    }
}