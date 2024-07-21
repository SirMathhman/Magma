package magma.app.compile.rule;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record OrRule(List<Rule> children) implements Rule {
    private Optional<Map<String, String>> parse0(String input) {
        for (Rule child : children) {
            var result = child.parse(input).map(Node::strings);
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }

    private Optional<String> generate0(Map<String, String> node) {
        for (Rule child : children) {
            var result = child.generate(new Node(Optional.empty(), node));
            if (result.isPresent()) return result;
        }

        return Optional.empty();
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
