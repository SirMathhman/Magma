package magma.rule;

import magma.compile.result.ResultFactory;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public final class SuffixRule<Node, Error> implements Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> {
    private final Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> rule;
    private final String suffix;
    private final ResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error, StringResult<Error>>> factory;

    public SuffixRule(final Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> rule,
                      final String suffix,
                      final ResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error, StringResult<Error>>> factory) {
        this.rule = rule;
        this.suffix = suffix;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> lex(final String input) {
        final var length = input.length();
        if (!input.endsWith(this.suffix))
            return this.factory.createNodeError("Suffix '" + this.suffix + "' not present", input);

        final var suffixLength = this.suffix.length();
        final var substring = input.substring(0, length - suffixLength);
        return this.rule.lex(substring);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.rule.generate(node).appendSlice(this.suffix);
    }
}