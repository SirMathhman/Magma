package magma.app.compile.rule.divide;

import magma.app.compile.node.NodeFactory;
import magma.app.compile.node.NodeWithNodeLists;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DivideRule<Node extends NodeWithNodeLists<Node>> implements Rule<Node> {
    private final String key;
    private final Rule<Node> rule;
    private final NodeFactory<Node> factory;

    public DivideRule(String key, Rule<Node> rule, NodeFactory<Node> factory) {
        this.key = key;
        this.rule = rule;
        this.factory = factory;
    }

    private static List<String> divide(CharSequence input) {
        DivideState current = new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static DivideState fold(DivideState state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    @Override
    public Optional<Node> lex(String input) {
        return divide(input).stream()
                .map(this.rule::lex)
                .reduce(Optional.<List<Node>>of(new ArrayList<>()), this::fold, (_, next) -> next)
                .map(children -> this.factory.create()
                        .nodeLists()
                        .with(this.key, children));
    }

    private Optional<List<Node>> fold(Optional<List<Node>> maybeCurrent, Optional<Node> maybeElement) {
        return maybeCurrent.flatMap(current -> maybeElement.map(element -> {
            current.add(element);
            return current;
        }));
    }

    @Override
    public Optional<String> generate(Node node) {
        final var children = node.nodeLists()
                .find(this.key)
                .orElse(new ArrayList<>());

        return Optional.of(children.stream()
                .map(this.rule::generate)
                .flatMap(Optional::stream)
                .collect(Collectors.joining()));
    }
}