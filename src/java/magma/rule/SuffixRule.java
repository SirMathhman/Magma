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
    
    @Override
    public Optional<Node> lex(final String input) {
        if (!input.endsWith(this.suffix)) {
            return Optional.empty();
        }
        
        String content = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(content);
    }
}