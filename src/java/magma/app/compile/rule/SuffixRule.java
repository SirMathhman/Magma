package magma.app.compile.rule;

import magma.app.compile.node.Node;
import magma.app.compile.result.CompileError;
import magma.app.compile.result.NodeErr;
import magma.app.compile.result.NodeResult;
import magma.app.compile.result.StringResult;

public record SuffixRule(Rule<Node> child, String suffix) implements Rule<Node> {
    @Override
    public NodeResult lex(final String input) {
        if (!input.endsWith(this.suffix))
            return new NodeErr(new CompileError("Suffix '" + this.suffix + "' not present", input));

        final var inputLength = input.length();
        final var suffixLength = this.suffix.length();
        final var slice = input.substring(0, inputLength - suffixLength);
        return this.child.lex(slice);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.child.generate(node)
                .appendSlice(this.suffix);
    }
}