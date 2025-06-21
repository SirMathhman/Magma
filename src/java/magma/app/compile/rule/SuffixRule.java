package magma.app.compile.rule;

import magma.app.compile.node.Node;
import magma.app.compile.result.CompileError;
import magma.app.compile.result.GenerateResult;
import magma.app.compile.result.LexErr;
import magma.app.compile.result.LexResult;

public record SuffixRule(Rule<Node> child, String suffix) implements Rule<Node> {
    @Override
    public LexResult lex(final String input) {
        if (!input.endsWith(this.suffix))
            return new LexErr(new CompileError("Suffix '" + this.suffix + "' not present", input));

        final var inputLength = input.length();
        final var suffixLength = this.suffix.length();
        final var slice = input.substring(0, inputLength - suffixLength);
        return this.child.lex(slice);
    }

    @Override
    public GenerateResult generate(final Node node) {
        return this.child.generate(node)
                .appendSlice(this.suffix);
    }
}