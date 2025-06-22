package magma.rule;

import magma.error.CompileError;
import magma.error.StringContext;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public record PrefixRule(String prefix,
                         Rule<EverythingNode, StringResult> rule) implements Rule<EverythingNode, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        if (!input.startsWith(this.prefix))
            return new NodeErr(new CompileError("Prefix '" + this.prefix + "' not present", new StringContext(input)));

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.rule.generate(node)
                .prepend(this.prefix);
    }
}