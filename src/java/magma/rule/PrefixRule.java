package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public record PrefixRule(String prefix, Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> rule)
        implements Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> {

    @Override
    public NodeResult<EverythingNode> lex(final String input) {
        if (!input.startsWith(this.prefix)) return NodeErr.create("Prefix '" + this.prefix + "' not present", input);
        final var prefixLength = this.prefix.length();

        final var slice = input.substring(prefixLength);
        return this.rule.lex(slice);
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.rule.generate(node).prependSlice(this.prefix);
    }
}