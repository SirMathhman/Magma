package magma.rule;

import magma.node.Node;

import java.util.Optional;

public final class PrefixRule implements Rule {
    private final Rule rule;
    private final String prefix;

    public PrefixRule(final Rule rule, final String prefix) {
        this.rule = rule;
        this.prefix = prefix;
    }

    @Override
    public Optional<String> generate(final Node node) {
        return this.rule.generate(node)
                .map(content -> this.prefix + content);
    }
    
    @Override
    public Optional<Node> lex(final String input) {
        if (!input.startsWith(this.prefix)) {
            return Optional.empty();
        }
        
        String content = input.substring(this.prefix.length());
        return this.rule.lex(content);
    }
}