package magma;

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
        final String body = new CommentRule(this.bodyRule).generate(node).orElse("");
        return this.nameRule.generate(node)
                .map(name -> "export class " + name + " {" + body + "}");
    }
}