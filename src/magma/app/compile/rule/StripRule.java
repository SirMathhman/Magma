package magma.app.compile.rule;

import java.util.Map;
import java.util.Optional;

public record StripRule(String left, Rule child, String right) implements Rule {
    private Optional<Map<String, String>> parse0(String input) {
        return child.parse(input.strip()).map(Node::strings);
    }

    private Optional<String> generate0(Map<String, String> node) {
        var leftSlice = node.getOrDefault(left, "");
        var rightSlice = node.getOrDefault(right, "");
        return child.generate(new Node(Optional.empty(), node)).map(inner -> leftSlice + inner + rightSlice);
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
