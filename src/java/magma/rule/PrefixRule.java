package magma.rule;

import magma.compile.result.NodeResultFactory;
import magma.string.result.ConcatStringResult;

public final class PrefixRule<Node, Error, StringResult extends ConcatStringResult<StringResult>, NodeResult, Factory extends NodeResultFactory<Node, Error, NodeResult>>
        implements Rule<Node, NodeResult, StringResult> {
    private final String prefix;
    private final Rule<Node, NodeResult, StringResult> rule;
    private final Factory resultFactory;

    public PrefixRule(final String prefix,
                      final Rule<Node, NodeResult, StringResult> rule,
                      final Factory resultFactory) {
        this.prefix = prefix;
        this.rule = rule;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult lex(final String input) {
        if (!input.startsWith(this.prefix))
            return this.resultFactory.createNodeError("Prefix '" + this.prefix + "' not present", input);
        final var prefixLength = this.prefix.length();

        final var slice = input.substring(prefixLength);
        return this.rule.lex(slice);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node).prependSlice(this.prefix);
    }
}