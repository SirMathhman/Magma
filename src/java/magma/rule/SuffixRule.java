package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public record SuffixRule(Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rule, String suffix)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {
    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        final var length = input.length();
        if (!input.endsWith(this.suffix)) return NodeErr.create("Suffix '" + this.suffix + "' not present", input);

        final var suffixLength = this.suffix.length();
        final var substring = input.substring(0, length - suffixLength);
        return this.rule().lex(substring);
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.rule.generate(node).appendSlice(this.suffix);
    }
}