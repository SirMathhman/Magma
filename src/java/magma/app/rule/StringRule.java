package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        Node node = new Node();
        return Optional.of(node.strings()
                .withString(this.key, input));
    }

    @Override
    public Optional<String> generate(Node node) {
        return node.strings()
                .findString(this.key);
    }
}