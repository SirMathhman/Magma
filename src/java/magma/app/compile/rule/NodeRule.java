package magma.app.compile.rule;

import magma.app.compile.node.MapNodeWithEverything;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record NodeRule(String key, Rule rule) implements Rule {
    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return node.findNode(this.key)
                .flatMap(this.rule::generate);
    }

    @Override
    public Optional<NodeWithEverything> lex(String input) {
        return this.rule.lex(input)
                .map(node -> new MapNodeWithEverything().withNode(this.key, node));
    }
}
