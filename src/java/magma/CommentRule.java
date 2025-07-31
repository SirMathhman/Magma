package magma;

import magma.node.Node;

import java.util.Optional;

public final class CommentRule implements Rule {
    private final Rule contentRule;

    public CommentRule(final Rule contentRule) {
        this.contentRule = contentRule;
    }

    @Override
    public Optional<String> generate(final Node node) {
        return this.contentRule.generate(node)
                .map(content -> "/*" + System.lineSeparator() + content + System.lineSeparator() + "*/");
    }
}