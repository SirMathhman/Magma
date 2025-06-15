package magma.app.compile.rule.divide;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record DivideRule(String key, Rule rule) implements Rule {
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
                .map(children -> new MapNode().nodeLists()
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