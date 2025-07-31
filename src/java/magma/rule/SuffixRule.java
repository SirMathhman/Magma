package magma.rule;

import magma.node.Node;

import java.util.Optional;

public final class SuffixRule implements Rule {
    private final Rule rule;
    private final String suffix;

    public SuffixRule(final Rule rule, final String suffix) {
        this.rule = rule;
        this.suffix = suffix;
    }

    @Override
    public Optional<String> generate(final Node node) {
        return this.rule.generate(node)
                .map(content -> content + this.suffix);
    }
}