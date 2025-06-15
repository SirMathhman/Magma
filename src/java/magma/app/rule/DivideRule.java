package magma.app.rule;

import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.Rule;
import magma.app.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record DivideRule(String key, Rule rule) implements Rule {
    private static List<String> divide(CharSequence input) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    @Override
    public Optional<Node> lex(String input) {
        final var children = divide(input).stream()
                .map(this.rule::lex)
                .flatMap(Optional::stream)
                .toList();

        Node node = new MapNode();
        return Optional.of(node.nodeLists()
                .with(this.key(), children));
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