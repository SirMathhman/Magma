package magma.app.compile.rule;

import magma.app.compile.factory.NodeResultFactory;
import magma.app.compile.string.Prepending;

public final class PrefixRule<Node, NodeResult, StringResult extends Prepending<StringResult>, Factory extends NodeResultFactory<Node, NodeResult>> implements
        Rule<Node, NodeResult, StringResult> {
    private final String prefix;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final Factory factory;

    public PrefixRule(final String prefix, final Rule<Node, NodeResult, StringResult> rule, final Factory factory) {
        this.prefix = prefix;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(final String input) {
        if (!input.startsWith(prefix))
            return factory.fromNodeError("Prefix '" + prefix + "' not present", input);

        final var slice = input.substring(prefix.length());
        return rule.lex(slice);
    }

    @Override
    public StringResult generate(final Node node) {
        return rule.generate(node)
                .prepend(prefix);
    }
}