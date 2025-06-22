package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.Appending;

public record SuffixRule<StringResult extends Appending<StringResult>>(Rule<Node, StringResult> rule,
                                                                       String suffix) implements Rule<Node, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        if (input.endsWith(this.suffix))
            return this.rule.lex(input.substring(0, input.length() - this.suffix.length()));

        return new NodeErr(new CompileError("?", "?"));
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node)
                .appendSlice(this.suffix);
    }
}
