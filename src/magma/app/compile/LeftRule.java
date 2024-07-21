package magma.app.compile;

import magma.app.compile.rule.Node;
import magma.app.compile.rule.Rule;

import java.util.Map;
import java.util.Optional;

public record LeftRule(String slice, Rule child) implements Rule {
    private Optional<Map<String, String>> parse0(String input) {
        if (!input.startsWith(slice)) return Optional.empty();
        return child.parse(input.substring(slice.length())).map(Node::strings);
    }

    private Optional<String> generate0(Map<String, String> node) {
        return child.generate(new Node(Optional.empty(), node)).map(inner -> slice + inner);
    }

    @Override
    public Optional<Node> parse(String input) {
        return parse0(input).map(strings -> new Node(Optional.empty(), strings));
    }

    @Override
    public Optional<String> generate(Node node) {
        return generate0(node.strings());
    }
}