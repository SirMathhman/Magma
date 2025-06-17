package magma.app.compile.rule;

import magma.app.compile.FormattedError;
import magma.app.compile.PrependStringResult;
import magma.app.compile.ResultFactory;

public final class PrefixRule<Node, NodeResult, StringResult extends PrependStringResult<StringResult>> implements Rule<Node, NodeResult, StringResult> {
    private final String prefix;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final ResultFactory<Node, FormattedError, NodeResult, StringResult> factory;

    public PrefixRule(String prefix, Rule<Node, NodeResult, StringResult> rule, ResultFactory<Node, FormattedError, NodeResult, StringResult> factory) {
        this.prefix = prefix;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        if (!input.startsWith(this.prefix))
            return this.factory.fromStringErr("Prefix '" + this.prefix + "' not present", input);

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public StringResult generate(Node node) {
        return this.rule.generate(node)
                .prepend(this.prefix);
    }
}