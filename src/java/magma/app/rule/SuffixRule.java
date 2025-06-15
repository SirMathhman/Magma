package magma.app.rule;

import magma.app.CompileError;
import magma.app.Rule;
import magma.app.maybe.NodeResult;
import magma.app.maybe.NodeResults;
import magma.app.maybe.string.Appendable;

public record SuffixRule<Node, Generate extends Appendable<Generate>>(
        Rule<Node, NodeResult<Node, CompileError>, Generate> rule,
        String suffix) implements Rule<Node, NodeResult<Node, CompileError>, Generate> {
    @Override
    public Generate generate(Node node) {
        return this.rule.generate(node).appendString(this.suffix);
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        if (!input.endsWith(this.suffix))
            return NodeResults.createFromString("Suffix '" + this.suffix + "' not present", input);

        return this.rule.lex(input.substring(0, input.length() - this.suffix.length()));
    }
}