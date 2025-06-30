package magma.rule;

import magma.api.Tuple;
import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.node.MapNode;
import magma.node.Node;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record DivideRule(String key, Rule rule) implements Rule {
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
    public Optional<Node> lex(final String input) {
        final var children = DivideRule.divide(input).map(this.rule::lex).flatMap(Optional::stream).toList();
        return Optional.of(new MapNode().withNodeList(this.key, children));
    }

    @Override
    public Optional<String> generate(final Node node) {
        return Optional.of(node.findNodeList(this.key)
                               .orElse(Collections.emptyList())
                               .stream()
                               .map(this.rule::generate)
                               .flatMap(Optional::stream)
                               .collect(Collectors.joining()));
    }
}