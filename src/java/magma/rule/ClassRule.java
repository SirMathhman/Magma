package magma.rule;

import magma.node.Node;

import java.util.Optional;

public final class ClassRule implements Rule {
    private final Rule nameRule;
    private final Rule bodyRule;

    public ClassRule(final Rule nameRule, final Rule bodyRule) {
        this.nameRule = nameRule;
        this.bodyRule = bodyRule;
    }

    @Override
    public Optional<String> generate(final Node node) {
        final Rule bodyWithPlaceholder = new PlaceholderRule(this.bodyRule);
        final Rule prefixedName = new PrefixRule(this.nameRule, "export class ");
        final Rule nameWithOpenBrace = new SuffixRule(prefixedName, " {");
        final Rule bodyWithCloseBrace = new SuffixRule(bodyWithPlaceholder, "}");
        
        return new InfixRule(nameWithOpenBrace, bodyWithCloseBrace, "").generate(node);
    }
}