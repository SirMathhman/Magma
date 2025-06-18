package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

import java.util.Optional;

public record NodeRule(String key, Rule rule) implements Rule {
    @Override
    public Optional<String> generate(Node node) {
        return node.findNode(this.key)
                .flatMap(this.rule::generate);
    }

    @Override
    public Optional<Node> lex(String input) {
        return this.rule.lex(input)
                .map(node -> new MapNode().withNode(this.key, node));
    }
}
