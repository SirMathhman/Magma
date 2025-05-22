package magmac.compile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record DivideRule(String key, Rule rule) implements Rule {
    public static List<String> divide(String input, State state) {
        var current = state;
        var length = input.length();
        for (var i = 0; i < length; i++) {
            var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments();
    }

    private static State fold(State state, char c) {
        var current = state.append(c);
        if (';' == c && state.isLevel()) {
            return current.advance();
        }
        if ('{' == c) {
            return current.enter();
        }
        if ('}' == c) {
            return current.exit();
        }
        return current;
    }

    @Override
    public Optional<MapNode> parse(String input) {
        var nodes = divide(input, new State())
                .stream()
                .map(segment -> this.rule().parse(segment))
                .flatMap(Optional::stream)
                .toList();

        var children = new MapNode().putNodeList(this.key(), nodes);
        return Optional.of(children);
    }

    @Override
    public Optional<String> generate(MapNode node) {
        return node.findNodeList(this.key()).map(children -> {
            return children.stream()
                    .map(child -> this.rule().generate(child))
                    .flatMap(Optional::stream)
                    .collect(Collectors.joining());
        });
    }
}