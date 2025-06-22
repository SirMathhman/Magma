package magma.app.compile.rule;

import magma.app.compile.node.Node;
import magma.app.compile.result.CompileError;
import magma.app.compile.result.NodeErr;
import magma.app.compile.result.NodeResult;
import magma.app.compile.result.StringResult;

public record PrefixRule(String prefix, Rule<Node> rule) implements Rule<Node> {
    @Override
    public NodeResult lex(final String input) {
        if (!input.startsWith(this.prefix))
            return new NodeErr(new CompileError("Prefix '" + this.prefix + "' not present", input));

        final var prefixLength = this.prefix.length();
        final var slice = input.substring(prefixLength);
        return this.rule.lex(slice);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node)
                .prependSlice(this.prefix);
    }
}