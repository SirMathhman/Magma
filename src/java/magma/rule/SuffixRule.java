package magma.rule;

import magma.compile.result.ResultFactory;
import magma.string.result.ConcatStringResult;

public final class SuffixRule<Node, Error, NodeResult, StringResult extends ConcatStringResult<StringResult>>
        implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> rule;
    private final String suffix;
    private final ResultFactory<Node, Error, StringResult, NodeResult> factory;

    public SuffixRule(final Rule<Node, NodeResult, StringResult> rule,
                      final String suffix,
                      final ResultFactory<Node, Error, StringResult, NodeResult> factory) {
        this.rule = rule;
        this.suffix = suffix;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(final String input) {
        final var length = input.length();
        if (!input.endsWith(this.suffix))
            return this.factory.createNodeError("Suffix '" + this.suffix + "' not present", input);

        final var suffixLength = this.suffix.length();
        final var substring = input.substring(0, length - suffixLength);
        return this.rule.lex(substring);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node).appendSlice(this.suffix);
    }
}