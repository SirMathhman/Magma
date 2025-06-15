package magma.app.rule;

import magma.app.CompileError;
import magma.app.Rule;
import magma.app.maybe.NodeResult;
import magma.app.maybe.node.ErrNodeResult;
import magma.app.maybe.string.Appendable;

public record SuffixRule<Node, Generate extends Appendable<Generate>>(Rule<Node, NodeResult, Generate> rule,
                                                                      String suffix) implements Rule<Node, NodeResult, Generate> {
    @Override
    public Generate generate(Node node) {
        return this.rule.generate(node).appendString(this.suffix);
    }

    @Override
    public NodeResult lex(String input) {
        if (!input.endsWith(this.suffix))
            return new ErrNodeResult(new CompileError("Suffix '" + this.suffix + "' not present", new StringContext(input)));

        return this.rule.lex(input.substring(0, input.length() - this.suffix.length()));
    }
}