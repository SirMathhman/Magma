package magma.rule;

import magma.compile.result.ResultFactory;
import magma.error.FormatError;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

import java.util.Objects;

public final class SuffixRule<Node> implements Rule<Node, NodeResult<Node>, StringResult<FormatError>> {
    private final Rule<Node, NodeResult<Node>, StringResult<FormatError>> rule;
    private final String suffix;
    private final ResultFactory<Node, StringResult<FormatError>> factory;

    public SuffixRule(final Rule<Node, NodeResult<Node>, StringResult<FormatError>> rule,
                      final String suffix,
                      final ResultFactory<Node, StringResult<FormatError>> factory) {
        this.rule = rule;
        this.suffix = suffix;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        final var length = input.length();
        if (!input.endsWith(this.suffix))
            return this.factory.createNodeError("Suffix '" + this.suffix + "' not present", input);

        final var suffixLength = this.suffix.length();
        final var substring = input.substring(0, length - suffixLength);
        return this.rule.lex(substring);
    }

    @Override
    public StringResult<FormatError> generate(final Node node) {
        return this.rule.generate(node).appendSlice(this.suffix);
    }

    public Rule<Node, NodeResult<Node>, StringResult<FormatError>> rule() {return this.rule;}

    public String suffix() {return this.suffix;}

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (null == obj || obj.getClass() != this.getClass()) return false;
        final var that = (SuffixRule<Node>) obj;
        return Objects.equals(this.rule, that.rule) && Objects.equals(this.suffix, that.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.rule, this.suffix);
    }

    @Override
    public String toString() {
        return "SuffixRule[" + "rule=" + this.rule + ", " + "suffix=" + this.suffix + ']';
    }

}