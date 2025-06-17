package magma.app.rule.divide;

import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.rule.Rule;

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

    private static DivideState fold(DivideState divideState, char c) {
        final var appended = divideState.append(c);
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

        return Optional.of(new MapNode().withNodeList(this.key(), children));
    }

    @Override
    public Optional<String> generate(Node node) {
        return Optional.of(node.findNodeList(this.key)
                .orElse(new ArrayList<>())
                .stream()
                .map(this.rule::generate)
                .flatMap(Optional::stream)
                .collect(Collectors.joining()));
    }
}