package magma.app.compile.rule;

import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record NodeRule(String key, Rule<NodeWithEverything> rule) implements Rule<NodeWithEverything> {
    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return node.findNode(this.key)
                .flatMap(this.rule::generate);
    }

    @Override
    public Optional<NodeWithEverything> lex(String input) {
        return this.rule.lex(input)
                .map(node -> new MapNodeFactory().create()
                        .withNode(this.key, node));
    }
}
