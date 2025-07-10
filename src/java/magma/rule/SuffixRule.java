package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record SuffixRule(PlaceholderRule rule, String key) {
    public Optional<String> generate(final Node node) {
        return this.rule.generate(node).map(value -> value + this.key);
    }
}