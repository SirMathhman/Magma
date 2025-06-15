package magma.app.compile.rule;

import magma.app.compile.CompileError;
import magma.app.compile.Node;
import magma.app.compile.NodeResult;
import magma.app.compile.Rule;
import magma.app.compile.StringResult;
import magma.app.compile.node.NodeResults;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public StringResult<CompileError> generate(Node node) {
        return this.rule.generate(node)
                .appendString(this.suffix);
    }

    @Override
    public NodeResult<Node, CompileError> lex(String input) {
        if (!input.endsWith(this.suffix))
            return NodeResults.createFromString("Suffix '" + this.suffix + "' not present", input);

        return this.rule.lex(input.substring(0, input.length() - this.suffix.length()));
    }
}