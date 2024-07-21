package magma.app.compile;

import magma.app.compile.rule.Node;
import magma.app.compile.rule.Rule;

import java.util.Map;
import java.util.Optional;

public record RightRule(Rule child, String slice) implements Rule {
    static Optional<String> truncateRight(String input, String slice) {
        if (!input.endsWith(slice)) return Optional.empty();
        return Optional.of(input.substring(0, input.length() - slice.length()));
    }

    private Optional<Map<String, String>> parse0(String input) {
        return truncateRight(input, slice()).flatMap(input1 -> this.child().parse(input1).map(Node::strings));
    }

    private Optional<String> generate0(Map<String, String> node) {
        return child.generate(new Node(node)).map(inner -> inner + slice);
    }

    @Override
    public Optional<Node> parse(String input) {
        return parse0(input).map(Node::new);
    }

    @Override
    public Optional<String> generate(Node node) {
        return generate0(node.strings());
    }
}