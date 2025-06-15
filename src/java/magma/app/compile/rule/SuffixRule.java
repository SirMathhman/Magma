package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Rule;
import magma.app.compile.NodeResult;
import magma.app.compile.node.NodeResults;
import magma.app.compile.string.Appending;

public record SuffixRule<Node, Generate extends Appending<Generate>>(
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